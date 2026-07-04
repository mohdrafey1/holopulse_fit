package com.lumastride.holopulsefit.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Serialization DTOs matching the provided sample data JSON files (schema.md section 6). These map
 * into the Room entities during first run seeding.
 */

/** One row of sample-workout-history.json. */
@Serializable
data class SampleWorkoutDto(
    @SerialName("session_id") val sessionId: String,
    val date: String,
    @SerialName("exercise_type") val exerciseType: String,
    @SerialName("duration_seconds") val durationSeconds: Int,
    val reps: Int,
    @SerialName("calories_estimate") val caloriesEstimate: Int,
    @SerialName("completed_status") val completedStatus: String,
)

/** sample-motion-paths.json: a simplified hip, knee, shoulder series for one session. */
@Serializable
data class SampleMotionPathDto(
    @SerialName("motion_path_id") val motionPathId: String,
    @SerialName("exercise_name") val exerciseName: String,
    @SerialName("landmark_series") val landmarkSeries: List<SampleSimplifiedFrameDto> = emptyList(),
    @SerialName("replay_label") val replayLabel: String = "sample guide only",
)

/** One simplified frame in sample-motion-paths.json. */
@Serializable
data class SampleSimplifiedFrameDto(
    @SerialName("timestamp_ms") val timestampMs: Long,
    @SerialName("hip_y") val hipY: Float? = null,
    @SerialName("knee_y") val kneeY: Float? = null,
    @SerialName("shoulder_y") val shoulderY: Float? = null,
)
