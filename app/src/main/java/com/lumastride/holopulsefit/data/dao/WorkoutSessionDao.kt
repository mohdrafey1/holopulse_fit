package com.lumastride.holopulsefit.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumastride.holopulsefit.data.entities.WorkoutSession
import kotlinx.coroutines.flow.Flow

/** DAO for [WorkoutSession] (schema.md section 4). */
@Dao
interface WorkoutSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WorkoutSession)

    @Query("SELECT * FROM WorkoutSession ORDER BY date DESC, id DESC")
    fun getAllByDateDesc(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM WorkoutSession WHERE id = :id")
    suspend fun getById(id: String): WorkoutSession?

    @Query("SELECT * FROM WorkoutSession ORDER BY date DESC, id DESC")
    suspend fun getAllOnce(): List<WorkoutSession>

    @Query("SELECT * FROM WorkoutSession ORDER BY date DESC, id DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<WorkoutSession>>

    @Query("DELETE FROM WorkoutSession WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM WorkoutSession")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM WorkoutSession")
    suspend fun count(): Int
}
