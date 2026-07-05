package com.lumastride.holopulsefit.data

import com.lumastride.holopulsefit.data.entities.ExerciseSet
import com.lumastride.holopulsefit.data.entities.MotionPath
import com.lumastride.holopulsefit.data.entities.Settings
import com.lumastride.holopulsefit.data.entities.UserStats
import com.lumastride.holopulsefit.data.entities.WorkoutSession
import com.lumastride.holopulsefit.ghost.MotionFrame
import com.lumastride.holopulsefit.ghost.MotionPathCodec
import com.lumastride.holopulsefit.ghost.SimplifiedSample
import com.lumastride.holopulsefit.util.StreakCalculator
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

/** Full data for one session, used by the History Detail screen. */
data class SessionDetail(
    val session: WorkoutSession,
    val exerciseSet: ExerciseSet?,
    val hasMotionPath: Boolean,
)

/** A resolved Ghost Trainer replay source: the frames to play plus whether they came from sample. */
data class ReplaySource(
    val frames: List<MotionFrame>,
    val replayLabel: String,
    val fromSample: Boolean,
)

/**
 * Single repository over the Room DAOs and the sample data loader (TRD MVVM). ViewModels talk only
 * to this class, never to DAOs or the camera. Cascade deletes remove linked ExerciseSet and
 * MotionPath rows; clearing history resets UserStats.
 */
class HoloRepository(
    private val db: HoloDatabase,
    private val sampleLoader: SampleDataLoader,
) {
    private val sessionDao = db.workoutSessionDao()
    private val setDao = db.exerciseSetDao()
    private val motionDao = db.motionPathDao()
    private val statsDao = db.userStatsDao()
    private val settingsDao = db.settingsDao()

    val allSessions: Flow<List<WorkoutSession>> = sessionDao.getAllByDateDesc()
    val sessionIdsWithPaths: Flow<List<String>> = motionDao.getSessionIdsWithPaths()
    val userStats: Flow<UserStats?> = statsDao.get()
    val settings: Flow<Settings?> = settingsDao.get()

    fun recentSessions(limit: Int): Flow<List<WorkoutSession>> = sessionDao.getRecent(limit)

    // region First run seeding

    /**
     * Seeds the database from the bundled sample data on the very first run (detected by an absent
     * Settings row) so the dashboard and history are populated for review. Clearing history later
     * does not trigger a reseed because the Settings row still exists.
     */
    suspend fun ensureInitialized() {
        if (settingsDao.getOnce() != null) return

        val sampleWorkouts = sampleLoader.loadWorkoutHistory()
        val sessions = sampleWorkouts.map { it.toEntity() }
        sessions.forEach { sessionDao.insert(it) }
        sampleWorkouts.forEach { dto ->
            setDao.insert(
                ExerciseSet(
                    id = UUID.randomUUID().toString(),
                    sessionId = dto.sessionId,
                    exerciseName = ExerciseType.fromId(dto.exerciseType).displayName,
                    targetReps = dto.reps,
                    countedReps = dto.reps,
                    confidenceAverage = 0.9f,
                ),
            )
        }

        // Attach the sample motion path to the matching seeded session so a replay always exists.
        val sampleMotion = sampleLoader.loadMotionPath()
        if (sampleMotion != null) {
            val frames = MotionPathCodec.fromSimplified(
                sampleMotion.landmarkSeries.map {
                    SimplifiedSample(it.timestampMs, it.hipY, it.kneeY, it.shoulderY)
                },
            )
            val target = sessions.firstOrNull { it.exerciseType == sampleMotion.exerciseName }
                ?: sessions.firstOrNull()
            if (target != null && frames.isNotEmpty()) {
                motionDao.insert(
                    MotionPath(
                        id = UUID.randomUUID().toString(),
                        sessionId = target.id,
                        landmarkSeries = MotionPathCodec.encodeSeries(frames),
                        timestamps = MotionPathCodec.encodeTimestamps(frames),
                        exerciseName = sampleMotion.exerciseName,
                        replayLabel = sampleMotion.replayLabel,
                    ),
                )
            }
        }

        statsDao.upsert(computeStats(sessions))
        settingsDao.upsert(Settings())
    }

    // endregion

    // region Session lifecycle

    /** One shot read of the current stats, for the summary screen. */
    suspend fun userStatsOnce(): UserStats? = statsDao.getOnce()

    suspend fun getSessionDetail(id: String): SessionDetail? {
        val session = sessionDao.getById(id) ?: return null
        val sets = setDao.getBySessionId(id)
        val paths = motionDao.getBySessionId(id)
        // Prefer the set for the session's primary exercise so the summary confidence matches it.
        val primaryName = ExerciseType.fromId(session.exerciseType).displayName
        val primarySet = sets.firstOrNull { it.exerciseName == primaryName } ?: sets.firstOrNull()
        return SessionDetail(session, primarySet, paths.isNotEmpty())
    }

    /**
     * Persists a finished session, its exercise result, and (when present) its downsampled motion
     * path, then updates the running stats and streak (schema.md section 5).
     */
    suspend fun saveSession(
        session: WorkoutSession,
        exerciseSets: List<ExerciseSet>,
        motionFrames: List<MotionFrame>,
        replayLabel: String,
    ) {
        sessionDao.insert(session)
        exerciseSets.forEach { setDao.insert(it) }
        if (motionFrames.isNotEmpty()) {
            motionDao.insert(
                MotionPath(
                    id = UUID.randomUUID().toString(),
                    sessionId = session.id,
                    landmarkSeries = MotionPathCodec.encodeSeries(motionFrames),
                    timestamps = MotionPathCodec.encodeTimestamps(motionFrames),
                    exerciseName = session.exerciseType,
                    replayLabel = replayLabel,
                ),
            )
        }

        val current = statsDao.getOnce() ?: UserStats()
        val last = current.lastWorkoutDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val today = runCatching { LocalDate.parse(session.date) }.getOrNull() ?: LocalDate.now()
        val streak = StreakCalculator.update(last, today, current.currentStreak, current.bestStreak)
        statsDao.upsert(
            current.copy(
                totalSessions = current.totalSessions + 1,
                currentStreak = streak.currentStreak,
                bestStreak = streak.bestStreak,
                totalCalories = current.totalCalories + session.caloriesEstimate,
                lastWorkoutDate = session.date,
            ),
        )
    }

    suspend fun deleteSession(id: String) {
        sessionDao.deleteById(id)
        statsDao.upsert(computeStats(sessionDao.getAllOnce()))
    }

    suspend fun clearAllHistory() {
        sessionDao.deleteAll()
        statsDao.upsert(UserStats())
    }

    // endregion

    // region Ghost Trainer replay source

    /**
     * Resolves the replay frames for an exercise: the latest saved motion path if one exists, else
     * the bundled sample path so replay is always demonstrable (appflow.md section 9.2).
     */
    suspend fun loadReplaySource(exerciseTypeId: String): ReplaySource {
        val saved = motionDao.getLatestByExercise(exerciseTypeId)
        if (saved != null) {
            val frames = MotionPathCodec.decodeSeries(saved.landmarkSeries)
            if (frames.isNotEmpty()) {
                return ReplaySource(frames, saved.replayLabel, fromSample = false)
            }
        }
        val sample = sampleLoader.loadMotionPath()
        val frames = sample?.let { mp ->
            MotionPathCodec.fromSimplified(
                mp.landmarkSeries.map { SimplifiedSample(it.timestampMs, it.hipY, it.kneeY, it.shoulderY) },
            )
        } ?: emptyList()
        val label = sample?.replayLabel ?: "Guidance only sample path"
        return ReplaySource(frames, label, fromSample = true)
    }

    // endregion

    // region Settings

    suspend fun setAuraIntensity(value: Float) = updateSettings { it.copy(auraIntensity = value) }
    suspend fun setReducedEffects(enabled: Boolean) = updateSettings { it.copy(reducedEffectsEnabled = enabled) }
    suspend fun setGhostTrainerEnabled(enabled: Boolean) = updateSettings { it.copy(ghostTrainerEnabled = enabled) }
    suspend fun setCameraPermissionState(state: String) = updateSettings { it.copy(cameraPermissionState = state) }

    private suspend fun updateSettings(transform: (Settings) -> Settings) {
        val current = settingsDao.getOnce() ?: Settings()
        settingsDao.upsert(transform(current))
    }

    // endregion

    private fun computeStats(sessions: List<WorkoutSession>): UserStats {
        if (sessions.isEmpty()) return UserStats()
        val dates = sessions.mapNotNull { runCatching { LocalDate.parse(it.date) }.getOrNull() }
        val streak = StreakCalculator.fromDates(dates)
        return UserStats(
            id = UserStats.SINGLETON_ID,
            totalSessions = sessions.size,
            currentStreak = streak.currentStreak,
            bestStreak = streak.bestStreak,
            totalCalories = sessions.sumOf { it.caloriesEstimate },
            lastWorkoutDate = dates.maxOrNull()?.toString(),
        )
    }

    private fun SampleWorkoutDto.toEntity(): WorkoutSession = WorkoutSession(
        id = sessionId,
        date = date,
        duration = durationSeconds,
        totalReps = reps,
        exerciseType = exerciseType,
        caloriesEstimate = caloriesEstimate,
        completedStatus = completedStatus,
    )
}
