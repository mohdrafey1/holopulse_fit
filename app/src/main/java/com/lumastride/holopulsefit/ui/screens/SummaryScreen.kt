package com.lumastride.holopulsefit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumastride.holopulsefit.ui.components.GlowCard
import com.lumastride.holopulsefit.ui.components.PrimaryGlowButton
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow
import com.lumastride.holopulsefit.ui.theme.VioletEnergy

/** Immutable summary state shown after a session ends. */
data class SummaryUi(
    val exerciseName: String,
    val exerciseTypeId: String,
    val reps: Int,
    val targetReps: Int,
    val durationLabel: String,
    val calories: Int,
    val streak: Int,
    val estimated: Boolean,
    val saved: Boolean,
    val hasMotionPath: Boolean,
)

/**
 * Session Summary: completed reps, duration, calories estimate, streak update, and save status,
 * with a completion glow moment (design.md Session Summary, appflow.md section 7).
 */
@Composable
fun SummaryScreen(
    state: SummaryUi,
    onBackToDashboard: () -> Unit,
    onRepeat: () -> Unit,
    onReplayGhost: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Session Complete",
            style = MaterialTheme.typography.headlineLarge,
            color = ElectricBlueGlow,
        )
        Text(
            text = state.exerciseName,
            style = MaterialTheme.typography.titleLarge,
            color = CyanPulse,
        )

        GlowCard(glowColor = ElectricBlueGlow, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryTile("${state.reps}", "reps", ElectricBlueGlow)
                SummaryTile(state.durationLabel, "duration", CyanPulse)
                SummaryTile("${state.calories}", "kcal", VioletEnergy)
            }
            if (state.estimated) {
                Text(
                    text = "Reps labeled estimated due to lower tracking confidence.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }

        GlowCard(glowColor = VioletEnergy, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Streak", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = if (state.saved) "Saved to history" else "Saving...",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (state.saved) CyanPulse else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "${state.streak} day${if (state.streak == 1) "" else "s"}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = VioletEnergy,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        PrimaryGlowButton(
            text = "Back to Dashboard",
            icon = Icons.Filled.Home,
            onClick = onBackToDashboard,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onRepeat, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Refresh, contentDescription = null, tint = CyanPulse)
                Text("  Repeat", color = MaterialTheme.colorScheme.onSurface)
            }
            OutlinedButton(onClick = onReplayGhost, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Replay, contentDescription = null, tint = VioletEnergy)
                Text("  Ghost", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun SummaryTile(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = value, style = MaterialTheme.typography.headlineLarge, color = color, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
