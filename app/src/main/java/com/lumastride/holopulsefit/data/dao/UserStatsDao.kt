package com.lumastride.holopulsefit.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lumastride.holopulsefit.data.entities.UserStats
import kotlinx.coroutines.flow.Flow

/** DAO for the single row [UserStats] aggregate (schema.md section 4). */
@Dao
interface UserStatsDao {

    @Query("SELECT * FROM UserStats WHERE id = 1")
    fun get(): Flow<UserStats?>

    @Query("SELECT * FROM UserStats WHERE id = 1")
    suspend fun getOnce(): UserStats?

    @Upsert
    suspend fun upsert(stats: UserStats)
}
