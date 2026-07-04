package com.lumastride.holopulsefit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import com.lumastride.holopulsefit.ui.SessionUi
import com.lumastride.holopulsefit.ui.components.GlowCard
import com.lumastride.holopulsefit.ui.components.PrimaryGlowButton
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow
import com.lumastride.holopulsefit.ui.theme.VioletEnergy
import com.lumastride.holopulsefit.ui.theme.WarningPulse

/**
 * History Detail: full session data, replay action when a motion path exists, and a delete action
 * in warning pulse styling with a confirmation dialog (appflow.md section 8). Delete cascades to
 * the linked ExerciseSet and MotionPath rows in the data layer.
 */
@Composable
fun HistoryDetailScreen(
    session: SessionUi,
    onReplay: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GlowCard(glowColor = ElectricBlueGlow, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = session.exerciseName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = session.dateLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DetailStat("${session.reps}", "reps", ElectricBlueGlow)
                DetailStat(session.durationLabel, "duration", CyanPulse)
                DetailStat("${session.calories}", "kcal", VioletEnergy)
            }
            Text(
                text = if (session.completed) "Completed" else "Stopped early",
                style = MaterialTheme.typography.labelLarge,
                color = if (session.completed) CyanPulse else WarningPulse,
                modifier = Modifier.padding(top = 14.dp),
            )
        }

        if (session.hasMotionPath) {
            PrimaryGlowButton(
                text = "Replay Ghost Trainer",
                icon = Icons.Filled.Replay,
                onClick = onReplay,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            GlowCard(glowColor = VioletEnergy, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No saved motion path for this session. Ghost Trainer will use a sample path.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedButton(onClick = onReplay, modifier = Modifier.padding(top = 10.dp)) {
                    Icon(Icons.Filled.Replay, contentDescription = null, tint = VioletEnergy)
                    Text("  Open Ghost Trainer", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        Button(
            onClick = { showConfirm = true },
            colors = ButtonDefaults.buttonColors(containerColor = WarningPulse, contentColor = MaterialTheme.colorScheme.onError),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null)
            Text("  Delete Session")
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    onDelete()
                }) { Text("Delete", color = WarningPulse) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancel", color = CyanPulse) }
            },
            title = { Text("Delete this session?", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    "This removes the session and its saved motion path. This cannot be undone.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}

@Composable
private fun DetailStat(value: String, label: String, color: Color) {
    Column {
        Text(text = value, style = MaterialTheme.typography.headlineMedium, color = color)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
