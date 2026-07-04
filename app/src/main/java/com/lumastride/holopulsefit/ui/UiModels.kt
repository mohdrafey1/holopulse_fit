package com.lumastride.holopulsefit.ui

/**
 * UI facing state models. These are intentionally separate from the Room entities so composables
 * never depend on the persistence layer directly (rules.md section 2, MVVM). ViewModels map
 * entities into these models. Phase 1 renders static instances; later phases feed live data.
 */

/** A single recent or history session row. */
data class SessionUi(
    val sessionId: String,
    val exerciseName: String,
    val exerciseTypeId: String,
    val dateLabel: String,
    val durationLabel: String,
    val reps: Int,
    val calories: Int,
    val completed: Boolean,
    val hasMotionPath: Boolean = false,
)

/** Dashboard aggregate state. */
data class DashboardUi(
    val greeting: String,
    val todayProgress: Float,
    val todayGoalLabel: String,
    val sessionsCompleted: Int,
    val currentStreak: Int,
    val recent: List<SessionUi>,
)

/** History screen aggregate state. */
data class HistoryUi(
    val totalSessions: Int,
    val totalCalories: Int,
    val bestStreak: Int,
    val sessions: List<SessionUi>,
)
