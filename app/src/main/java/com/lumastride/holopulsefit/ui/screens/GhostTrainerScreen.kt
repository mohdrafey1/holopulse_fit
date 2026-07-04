package com.lumastride.holopulsefit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lumastride.holopulsefit.ui.components.GhostSkeleton
import com.lumastride.holopulsefit.ui.components.GlowCard
import com.lumastride.holopulsefit.ui.components.PrimaryGlowButton
import com.lumastride.holopulsefit.ui.theme.CardSurface
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.VioletEnergy
import com.lumastride.holopulsefit.ui.viewmodel.GhostReplayUi

/**
 * Ghost Trainer Replay stage (design.md Ghost Trainer, appflow.md section 9). A dark stage plays the
 * saved or sample motion path as a semi transparent violet skeleton looped and interpolated from its
 * timestamps, with playback control, a timing progress bar, and the persistent guidance only label
 * required by rules.md section 6.4.
 */
@Composable
fun GhostTrainerScreen(
    state: GhostReplayUi,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.exercise.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            if (state.fromSample) {
                Text(
                    text = "sample path",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyanPulse,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(CardSurface),
            contentAlignment = Alignment.Center,
        ) {
            if (state.frame.isNotEmpty()) {
                GhostSkeleton(frame = state.frame, modifier = Modifier.fillMaxSize(), alpha = 0.7f)
            } else {
                Text(
                    text = if (state.loaded) "No motion to replay" else "Loading guide...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = VioletEnergy,
            trackColor = MaterialTheme.colorScheme.outline,
        )

        PrimaryGlowButton(
            text = if (state.playing) "Pause" else "Play",
            icon = if (state.playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            onClick = onTogglePlay,
            modifier = Modifier.fillMaxWidth(),
        )

        // Persistent guidance only label (rules.md 6.4).
        GlowCard(glowColor = VioletEnergy, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Guidance only. The Ghost Trainer is a movement guide, not medical or " +
                    "professional fitness correction.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
