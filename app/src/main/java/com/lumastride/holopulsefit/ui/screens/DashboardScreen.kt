package com.lumastride.holopulsefit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lumastride.holopulsefit.ui.DashboardUi
import com.lumastride.holopulsefit.ui.SessionUi
import com.lumastride.holopulsefit.ui.components.GlowCard
import com.lumastride.holopulsefit.ui.components.PrimaryGlowButton
import com.lumastride.holopulsefit.ui.components.ProgressRing
import com.lumastride.holopulsefit.ui.components.StreakBadge
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow
import com.lumastride.holopulsefit.ui.theme.VioletEnergy

/**
 * Dashboard: greeting, today progress ring, streak badge, a dominant quick start card, and recent
 * session cards (design.md Dashboard, appflow.md section 3). Recent cards open history detail.
 */
@Composable
fun DashboardScreen(
    state: DashboardUi,
    onQuickStart: () -> Unit,
    onOpenSession: (SessionUi) -> Unit,
    onOpenGhost: () -> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = state.greeting,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Ready to move?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = CyanPulse,
                )
            }
            StreakBadge(streak = state.currentStreak)
        }

        // Today progress card.
        GlowCard(glowColor = CyanPulse, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProgressRing(
                    progress = state.todayProgress,
                    diameter = 104.dp,
                    centerContent = {
                        Text(
                            text = "${(state.todayProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            color = ElectricBlueGlow,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = state.todayGoalLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${state.sessionsCompleted} sessions done",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyanPulse,
                    )
                }
            }
        }

        PrimaryGlowButton(
            text = "Quick Start Workout",
            icon = Icons.Filled.PlayArrow,
            onClick = onQuickStart,
            modifier = Modifier.fillMaxWidth(),
        )

        GlowCard(
            glowColor = VioletEnergy,
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenGhost,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Filled.Replay, contentDescription = null, tint = VioletEnergy, modifier = Modifier.size(28.dp))
                Column {
                    Text(
                        text = "AI Ghost Trainer",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Replay a saved motion path as a guide",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 4.dp),
        )

        if (state.recent.isEmpty()) {
            EmptyRecent()
        } else {
            state.recent.forEach { session ->
                RecentSessionCard(session = session, onClick = { onOpenSession(session) })
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun RecentSessionCard(session: SessionUi, onClick: () -> Unit) {
    GlowCard(
        glowColor = ElectricBlueGlow,
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
                    text = session.dateLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                StatColumn(value = "${session.reps}", label = "reps")
                StatColumn(value = session.durationLabel, label = "time")
                StatColumn(value = "${session.calories}", label = "kcal")
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = ElectricBlueGlow)
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyRecent() {
    GlowCard(glowColor = CyanPulse, modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "No workouts yet. Tap Quick Start to begin your first session.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
