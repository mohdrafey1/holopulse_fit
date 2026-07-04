package com.lumastride.holopulsefit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lumastride.holopulsefit.data.dao.ExerciseSetDao
import com.lumastride.holopulsefit.data.dao.MotionPathDao
import com.lumastride.holopulsefit.data.dao.SettingsDao
import com.lumastride.holopulsefit.data.dao.UserStatsDao
import com.lumastride.holopulsefit.data.dao.WorkoutSessionDao
import com.lumastride.holopulsefit.data.entities.ExerciseSet
import com.lumastride.holopulsefit.data.entities.MotionPath
import com.lumastride.holopulsefit.data.entities.Settings
import com.lumastride.holopulsefit.data.entities.UserStats
import com.lumastride.holopulsefit.data.entities.WorkoutSession

/**
 * The single local database `holopulse_fit.db` (schema.md). All data is local to the device; no
 * raw camera frames are ever stored. Foreign key cascade deletes remove the child ExerciseSet and
 * MotionPath rows when a WorkoutSession is deleted.
 */
@Database(
    entities = [
        WorkoutSession::class,
        ExerciseSet::class,
        MotionPath::class,
        UserStats::class,
        Settings::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class HoloDatabase : RoomDatabase() {
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun exerciseSetDao(): ExerciseSetDao
    abstract fun motionPathDao(): MotionPathDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        private const val DB_NAME = "holopulse_fit.db"

        @Volatile
        private var instance: HoloDatabase? = null

        fun getInstance(context: Context): HoloDatabase =
            instance ?: synchronized(this) {
                instance ?: build(context).also { instance = it }
            }

        private fun build(context: Context): HoloDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                HoloDatabase::class.java,
                DB_NAME,
            ).build()
    }
}
