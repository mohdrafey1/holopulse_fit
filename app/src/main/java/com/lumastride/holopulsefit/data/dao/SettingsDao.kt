package com.lumastride.holopulsefit.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.lumastride.holopulsefit.data.entities.Settings
import kotlinx.coroutines.flow.Flow

/** DAO for the single row [Settings] preferences (schema.md section 4). */
@Dao
interface SettingsDao {

    @Query("SELECT * FROM Settings WHERE id = 1")
    fun get(): Flow<Settings?>

    @Query("SELECT * FROM Settings WHERE id = 1")
    suspend fun getOnce(): Settings?

    @Upsert
    suspend fun upsert(settings: Settings)
}
