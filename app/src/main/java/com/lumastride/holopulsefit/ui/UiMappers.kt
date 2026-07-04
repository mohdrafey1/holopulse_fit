package com.lumastride.holopulsefit.ui

import com.lumastride.holopulsefit.data.ExerciseType
import com.lumastride.holopulsefit.data.entities.WorkoutSession
import com.lumastride.holopulsefit.util.TimeFormat

/** Maps a persisted [WorkoutSession] into the UI facing [SessionUi]. */
fun WorkoutSession.toSessionUi(hasMotionPath: Boolean): SessionUi = SessionUi(
    sessionId = id,
    exerciseName = ExerciseType.fromId(exerciseType).displayName,
    exerciseTypeId = exerciseType,
    dateLabel = TimeFormat.displayDate(date),
    durationLabel = TimeFormat.duration(duration),
    reps = totalReps,
    calories = caloriesEstimate,
    completed = completedStatus == WorkoutSession.STATUS_COMPLETED,
    hasMotionPath = hasMotionPath,
)

/** A time of day greeting for the dashboard. */
fun greetingForHour(hour: Int): String = when (hour) {
    in 5..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    in 17..21 -> "Good evening"
    else -> "Welcome back"
}
