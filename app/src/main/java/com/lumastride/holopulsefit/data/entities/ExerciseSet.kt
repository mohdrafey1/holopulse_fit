package com.lumastride.holopulsefit.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * The exercise result for a session (schema.md section 2.2). Deleting the parent
 * [WorkoutSession] cascades to this row.
 */
@Entity(
    tableName = "ExerciseSet",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["sessionId"], name = "index_exerciseset_sessionId")],
)
data class ExerciseSet(
    @PrimaryKey val id: String,
    val sessionId: String,
    val exerciseName: String,
    /** User selected target reps. */
    val targetReps: Int,
    /** Actual counted reps. */
    val countedReps: Int,
    /** 0.0 to 1.0; low values mark results as estimated. */
    val confidenceAverage: Float,
)
