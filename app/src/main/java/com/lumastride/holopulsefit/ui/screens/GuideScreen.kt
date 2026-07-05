package com.lumastride.holopulsefit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.lumastride.holopulsefit.ui.components.GlowCard
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow
import com.lumastride.holopulsefit.ui.theme.VioletEnergy
import com.lumastride.holopulsefit.ui.theme.WarningPulse

/**
 * How it works guide (opened from Settings). Explains the touchless gestures, rep counting, Aura
 * Energy, the Ghost Trainer, and privacy so the app is discoverable without a tutorial overlay.
 */
@Composable
fun GuideScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader("Touchless Control")
        GuideCard(
            icon = Icons.Filled.BackHand,
            glow = ElectricBlueGlow,
            title = "Raise one hand",
            body = "Lift a single hand clearly above your shoulder and hold for about a second to " +
                "finish the session and see your summary.",
        )
        GuideCard(
            icon = Icons.Filled.PanTool,
            glow = CyanPulse,
            title = "Raise both hands",
            body = "Lift both hands above your shoulders and hold for about a second to pause. Lower " +
                "them and raise again to resume.",
        )
        GuideCard(
            icon = Icons.Filled.Swipe,
            glow = VioletEnergy,
            title = "Swipe to switch exercise",
            body = "Hold one arm out to the side at shoulder height and sweep it left or right to " +
                "move to the previous or next exercise. Your reps so far are kept in the session total.",
        )
        GuideCard(
            icon = Icons.Filled.TouchApp,
            glow = WarningPulse,
            title = "Deliberate by design",
            body = "Every gesture needs a short stable hold or a clear sweep, and there is a short " +
                "cooldown after each one so a single motion never triggers twice. Every gesture also " +
                "has an on screen touch button, so you are never stuck.",
        )

        SectionHeader("Rep Counting")
        GuideCard(
            icon = Icons.Filled.FitnessCenter,
            glow = ElectricBlueGlow,
            title = "Squats, jumping jacks, push-ups",
            body = "The camera reads your body pose and counts a rep only after a complete movement " +
                "cycle, so half reps do not count. Push-ups are approximate and are labeled estimated " +
                "when confidence is low. If your whole body is not visible or the light is low, " +
                "counting pauses and a guidance message appears.",
        )

        SectionHeader("Visuals")
        GuideCard(
            icon = Icons.Filled.Bolt,
            glow = CyanPulse,
            title = "Aura Energy",
            body = "Glowing trails follow your wrists, elbows, knees, and ankles and grow with your " +
                "movement speed. Turn on Reduced effects in Settings for a lighter look and better " +
                "performance.",
        )
        GuideCard(
            icon = Icons.Filled.Replay,
            glow = VioletEnergy,
            title = "AI Ghost Trainer",
            body = "The app saves a simplified skeleton path from your session. The Ghost Trainer " +
                "replays it as a semi transparent violet guide you can follow, with a match cue " +
                "during a workout. It is guidance only, not medical or professional correction. When " +
                "you have no saved path yet, a bundled sample path is shown.",
        )

        SectionHeader("Privacy")
        GuideCard(
            icon = Icons.Filled.Lock,
            glow = ElectricBlueGlow,
            title = "On device only",
            body = "Camera frames are analyzed on your device to read your pose. No video or images " +
                "are ever stored or uploaded. Only session summaries, exercise results, settings, and " +
                "simplified motion paths are saved locally, and you can delete them any time.",
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 6.dp),
    )
}

@Composable
private fun GuideCard(icon: ImageVector, glow: Color, title: String, body: String) {
    GlowCard(glowColor = glow, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = glow, modifier = Modifier.size(28.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
