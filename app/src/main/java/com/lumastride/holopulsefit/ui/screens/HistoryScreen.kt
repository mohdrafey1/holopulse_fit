package com.lumastride.holopulsefit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lumastride.holopulsefit.ui.HistoryUi
import com.lumastride.holopulsefit.ui.SessionUi
import com.lumastride.holopulsefit.ui.components.GlowCard
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow
import com.lumastride.holopulsefit.ui.theme.VioletEnergy
import com.lumastride.holopulsefit.ui.theme.WarningPulse

/**
 * Workout History list with a progress summary strip and compact session cards (design.md History,
 * appflow.md section 8). Tapping a card opens the detail view. Empty history shows a friendly state.
 */
@Composable
fun HistoryScreen(
    state: HistoryUi,
    onOpenSession: (SessionUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Progress summary strip.
        GlowCard(glowColor = CyanPulse, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                SummaryStat(value = "${state.totalSessions}", label = "sessions", color = ElectricBlueGlow)
                SummaryStat(value = "${state.totalCalories}", label = "kcal total", color = CyanPulse)
                SummaryStat(value = "${state.bestStreak}", label = "best streak", color = VioletEnergy)
            }
        }

        if (state.sessions.isEmpty()) {
            EmptyHistory()
        } else {
            state.sessions.forEach { session ->
                HistorySessionCard(session = session, onClick = { onOpenSession(session) })
            }
        }
    }
}

@Composable
private fun SummaryStat(value: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineMedium, color = color)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HistorySessionCard(session: SessionUi, onClick: () -> Unit) {
    GlowCard(
        glowColor = if (session.completed) ElectricBlueGlow else WarningPulse,
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = session.exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${session.dateLabel}  ${if (session.completed) "" else "stopped early"}".trim(),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (session.completed) MaterialTheme.colorScheme.onSurfaceVariant else WarningPulse,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MiniStat("${session.reps}", "reps")
                MiniStat(session.durationLabel, "time")
                MiniStat("${session.calories}", "kcal")
            }
        }
    }
}

@Composable
private fun MiniStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = ElectricBlueGlow)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyHistory() {
    GlowCard(glowColor = CyanPulse, modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "No sessions saved yet.\nComplete a workout to start building your history and streak.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
