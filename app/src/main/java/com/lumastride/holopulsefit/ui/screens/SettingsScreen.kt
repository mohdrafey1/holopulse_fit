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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lumastride.holopulsefit.ui.components.GlowCard
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow
import com.lumastride.holopulsefit.ui.theme.VioletEnergy
import com.lumastride.holopulsefit.ui.theme.WarningPulse

/** Immutable snapshot of user settings for the Settings screen. */
data class SettingsUi(
    val cameraPermissionLabel: String,
    val auraIntensity: Float,
    val reducedEffectsEnabled: Boolean,
    val ghostTrainerEnabled: Boolean,
)

/**
 * Settings: permission status, aura intensity, reduced effects, ghost trainer toggle, clear
 * history, and privacy notes (design.md Settings, appflow.md section 10).
 */
@Composable
fun SettingsScreen(
    state: SettingsUi,
    onAuraIntensityChange: (Float) -> Unit,
    onReducedEffectsChange: (Boolean) -> Unit,
    onGhostTrainerChange: (Boolean) -> Unit,
    onOpenPermissionSettings: () -> Unit,
    onOpenGuide: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showClearConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // How it works guide.
        GlowCard(glowColor = CyanPulse, modifier = Modifier.fillMaxWidth(), onClick = onOpenGuide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.padding(end = 12.dp)) {
                    SectionTitle("How HoloPulse Fit works")
                    Text(
                        text = "Gestures, rep counting, Aura, and Ghost Trainer explained",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = CyanPulse,
                )
            }
        }

        // Camera permission.
        GlowCard(glowColor = ElectricBlueGlow, modifier = Modifier.fillMaxWidth()) {
            SectionTitle("Camera Permission")
            Text(
                text = state.cameraPermissionLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = CyanPulse,
                modifier = Modifier.padding(vertical = 6.dp),
            )
            TextButton(onClick = onOpenPermissionSettings) {
                Text("Open camera permission settings", color = ElectricBlueGlow)
            }
        }

        // Effects.
        GlowCard(glowColor = VioletEnergy, modifier = Modifier.fillMaxWidth()) {
            SectionTitle("Aura Energy")
            Text(
                text = "Intensity",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
            Slider(
                value = state.auraIntensity,
                onValueChange = onAuraIntensityChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = VioletEnergy,
                    activeTrackColor = VioletEnergy,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline,
                ),
            )
            ToggleRow(
                label = "Reduced effects",
                description = "Shorter trails, lower glow, animations minimized",
                checked = state.reducedEffectsEnabled,
                onCheckedChange = onReducedEffectsChange,
            )
        }

        // Ghost Trainer.
        GlowCard(glowColor = CyanPulse, modifier = Modifier.fillMaxWidth()) {
            SectionTitle("AI Ghost Trainer")
            ToggleRow(
                label = "Enable Ghost Trainer",
                description = "Replay saved motion paths as a guide",
                checked = state.ghostTrainerEnabled,
                onCheckedChange = onGhostTrainerChange,
            )
        }

        // History management.
        GlowCard(glowColor = WarningPulse, modifier = Modifier.fillMaxWidth()) {
            SectionTitle("History")
            Text(
                text = "Clear all local workout records. This also resets your stats and streak.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 6.dp),
            )
            Button(
                onClick = { showClearConfirm = true },
                colors = ButtonDefaults.buttonColors(containerColor = WarningPulse, contentColor = MaterialTheme.colorScheme.onError),
            ) {
                Icon(Icons.Filled.DeleteSweep, contentDescription = null)
                Text("  Clear all history")
            }
        }

        // Privacy.
        GlowCard(glowColor = ElectricBlueGlow, modifier = Modifier.fillMaxWidth()) {
            SectionTitle("Privacy")
            Text(
                text = "Camera frames are analyzed on device only. No video or images are ever " +
                    "stored. HoloPulse Fit saves only session summaries, exercise results, " +
                    "settings, and simplified motion paths.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            confirmButton = {
                TextButton(onClick = {
                    showClearConfirm = false
                    onClearHistory()
                }) { Text("Clear all", color = WarningPulse) }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text("Cancel", color = CyanPulse) }
            },
            title = { Text("Clear all history?", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    "Every saved session, motion path, and your streak will be removed. This cannot be undone.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun ToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.padding(end = 12.dp)) {
            Text(text = label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = description, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = ElectricBlueGlow,
            ),
        )
    }
}
