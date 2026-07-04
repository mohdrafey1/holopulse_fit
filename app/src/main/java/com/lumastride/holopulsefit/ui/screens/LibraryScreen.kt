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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lumastride.holopulsefit.data.ExerciseType
import com.lumastride.holopulsefit.ui.components.GlowCard
import com.lumastride.holopulsefit.ui.components.PrimaryGlowButton
import com.lumastride.holopulsefit.ui.theme.CyanPulse
import com.lumastride.holopulsefit.ui.theme.ElectricBlueGlow
import com.lumastride.holopulsefit.ui.theme.VioletEnergy
import com.lumastride.holopulsefit.ui.theme.WarningPulse

/**
 * Workout Library: a GlowCard per supported exercise with a short instruction, selectable target
 * rep chips, an effort tag, and a bright start button (design.md Workout Library, appflow.md
 * section 4). Cards scroll by touch; side swipe gestures reuse the same list during a session.
 */
@Composable
fun LibraryScreen(
    onStart: (ExerciseType, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Selected target reps per exercise, defaulting to each exercise's default.
    val selectedTargets = remember {
        mutableStateMapOf<ExerciseType, Int>().apply {
            ExerciseType.entries.forEach { put(it, it.defaultTargetReps) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ExerciseType.entries.forEachIndexed { index, exercise ->
            val glow = when (index % 3) {
                0 -> ElectricBlueGlow
                1 -> CyanPulse
                else -> VioletEnergy
            }
            ExerciseCard(
                exercise = exercise,
                glowColor = glow,
                selectedTarget = selectedTargets[exercise] ?: exercise.defaultTargetReps,
                onTargetSelected = { selectedTargets[exercise] = it },
                onStart = { onStart(exercise, selectedTargets[exercise] ?: exercise.defaultTargetReps) },
            )
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseType,
    glowColor: androidx.compose.ui.graphics.Color,
    selectedTarget: Int,
    onTargetSelected: (Int) -> Unit,
    onStart: () -> Unit,
) {
    GlowCard(glowColor = glowColor, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = exercise.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            EffortTag(effort = exercise.effort)
        }
        Text(
            text = exercise.shortInstruction,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp),
        )
        Text(
            text = "Target reps",
            style = MaterialTheme.typography.labelMedium,
            color = CyanPulse,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 6.dp, bottom = 14.dp),
        ) {
            exercise.targetRepOptions.forEach { option ->
                FilterChip(
                    selected = option == selectedTarget,
                    onClick = { onTargetSelected(option) },
                    label = { Text("$option") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = glowColor,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            }
            if (exercise.approximate) {
                Text(
                    text = "estimated",
                    style = MaterialTheme.typography.labelMedium,
                    color = WarningPulse,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
        }
        PrimaryGlowButton(
            text = "Start ${exercise.displayName}",
            icon = Icons.Filled.PlayArrow,
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun EffortTag(effort: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(Icons.Filled.Bolt, contentDescription = null, tint = WarningPulse, modifier = Modifier.padding(end = 2.dp))
        Text(
            text = effort,
            style = MaterialTheme.typography.labelMedium,
            color = WarningPulse,
        )
    }
}
