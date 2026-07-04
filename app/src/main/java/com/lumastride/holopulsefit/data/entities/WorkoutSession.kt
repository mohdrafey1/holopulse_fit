package com.lumastride.holopulsefit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A completed or stopped workout session (schema.md section 2.1). Entity name is fixed by the
 * instruction document and must not change.
 */
@Entity(tableName = "WorkoutSession")
data class WorkoutSession(
    @PrimaryKey val id: String,
    /** ISO date, for example 2026-06-13. */
    val date: String,
    /** Duration in seconds. */
    val duration: Int,
    /** Sum of counted reps across the session. */
    val totalReps: Int,
    /** One of squats, jumping_jacks, pushup_approximation. */
    val exerciseType: String,
    /** Estimated kcal, stored at save time. */
    val caloriesEstimate: Int,
    /** completed or stopped_early. */
    val completedStatus: String,
) {
    companion object {
        const val STATUS_COMPLETED = "completed"
        const val STATUS_STOPPED_EARLY = "stopped_early"
    }
}
