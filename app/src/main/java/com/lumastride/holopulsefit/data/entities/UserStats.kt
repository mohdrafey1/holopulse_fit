package com.lumastride.holopulsefit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single row aggregate of lifetime stats (schema.md section 2.4). The id is always 1.
 */
@Entity(tableName = "UserStats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val totalSessions: Int = 0,
    /** Consecutive workout days. */
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCalories: Int = 0,
    /** ISO date of the last workout, drives streak update logic. Null before the first session. */
    val lastWorkoutDate: String? = null,
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
