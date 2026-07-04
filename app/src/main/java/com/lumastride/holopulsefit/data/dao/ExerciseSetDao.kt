package com.lumastride.holopulsefit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumastride.holopulsefit.data.entities.ExerciseSet

/** DAO for [ExerciseSet] (schema.md section 4). */
@Dao
interface ExerciseSetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exerciseSet: ExerciseSet)

    @Query("SELECT * FROM ExerciseSet WHERE sessionId = :sessionId")
    suspend fun getBySessionId(sessionId: String): List<ExerciseSet>
}
