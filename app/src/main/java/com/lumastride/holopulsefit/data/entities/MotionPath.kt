package com.lumastride.holopulsefit.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A simplified skeletal motion path for Ghost Trainer replay (schema.md section 2.3). Only
 * normalized joint coordinates and timestamps are stored, never raw camera frames. Deleting the
 * parent [WorkoutSession] cascades to this row.
 */
@Entity(
    tableName = "MotionPath",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["sessionId"], name = "index_motionpath_sessionId")],
)
data class MotionPath(
    @PrimaryKey val id: String,
    val sessionId: String,
    /** JSON array of sampled landmark frames (schema.md section 3). */
    val landmarkSeries: String,
    /** JSON array of sample timestamps in ms, aligned with landmarkSeries. */
    val timestamps: String,
    /** Used to match a replay to the same workout. */
    val exerciseName: String,
    /** Guidance label shown during replay. */
    val replayLabel: String,
)
