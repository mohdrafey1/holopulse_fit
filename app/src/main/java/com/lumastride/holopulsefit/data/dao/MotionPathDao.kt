package com.lumastride.holopulsefit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumastride.holopulsefit.data.entities.MotionPath
import kotlinx.coroutines.flow.Flow

/** DAO for [MotionPath] (schema.md section 4). */
@Dao
interface MotionPathDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(motionPath: MotionPath)

    /** Distinct session ids that have a saved motion path, used to flag replayable sessions. */
    @Query("SELECT DISTINCT sessionId FROM MotionPath")
    fun getSessionIdsWithPaths(): Flow<List<String>>

    @Query("SELECT * FROM MotionPath WHERE sessionId = :sessionId")
    suspend fun getBySessionId(sessionId: String): List<MotionPath>

    /** Most recent saved path for an exercise, used to match Ghost Trainer replay. */
    @Query(
        "SELECT mp.* FROM MotionPath mp " +
            "INNER JOIN WorkoutSession ws ON mp.sessionId = ws.id " +
            "WHERE mp.exerciseName = :exerciseName " +
            "ORDER BY ws.date DESC, ws.id DESC LIMIT 1",
    )
    suspend fun getLatestByExercise(exerciseName: String): MotionPath?

    @Query("DELETE FROM MotionPath WHERE sessionId = :sessionId")
    suspend fun deleteBySessionId(sessionId: String)
}
