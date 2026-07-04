package com.lumastride.holopulsefit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single row of user preferences (schema.md section 2.5). The id is always 1.
 */
@Entity(tableName = "Settings")
data class Settings(
    @PrimaryKey val id: Int = 1,
    /** unknown, granted, denied, permanently_denied. */
    val cameraPermissionState: String = PERMISSION_UNKNOWN,
    /** 0.0 to 1.0 slider value. */
    val auraIntensity: Float = 1.0f,
    val ghostTrainerEnabled: Boolean = true,
    val reducedEffectsEnabled: Boolean = false,
) {
    companion object {
        const val SINGLETON_ID = 1
        const val PERMISSION_UNKNOWN = "unknown"
        const val PERMISSION_GRANTED = "granted"
        const val PERMISSION_DENIED = "denied"
        const val PERMISSION_PERMANENTLY_DENIED = "permanently_denied"
    }
}
