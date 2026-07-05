package com.lumastride.holopulsefit.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.ui.geometry.Offset
import com.lumastride.holopulsefit.counting.RepCounterFactory
import com.lumastride.holopulsefit.data.ExerciseType
import com.lumastride.holopulsefit.data.HoloRepository
import com.lumastride.holopulsefit.data.entities.ExerciseSet
import com.lumastride.holopulsefit.data.entities.Settings
import com.lumastride.holopulsefit.data.entities.WorkoutSession
import com.lumastride.holopulsefit.gesture.GestureDetector
import com.lumastride.holopulsefit.gesture.GestureType
import com.lumastride.holopulsefit.ghost.GhostReplay
import com.lumastride.holopulsefit.ghost.MotionFrame
import com.lumastride.holopulsefit.ghost.MotionRecorder
import com.lumastride.holopulsefit.ghost.toGhostOffsets
import com.lumastride.holopulsefit.navigation.HoloDestinations
import com.lumastride.holopulsefit.pose.PoseConfidence
import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.util.CalorieEstimator
import com.lumastride.holopulsefit.util.TimeFormat
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Phases of a camera workout session (appflow.md section 6). */
enum class WorkoutPhase { COUNTDOWN, TRACKING, GUIDANCE, PAUSED, COMPLETE }

/**
 * Screen level action produced by a touchless gesture that requires navigation. Exercise switching
 * is handled in place inside the ViewModel and does not appear here.
 */
enum class WorkoutGesture { FINISH }

/** Immutable state driving the camera workout screen. */
data class WorkoutUiState(
    val exercise: ExerciseType,
    val phase: WorkoutPhase,
    val countdown: Int,
    val reps: Int,
    val targetReps: Int,
    /** Reps completed earlier in this session under previous exercises (before a swipe switch). */
    val previousReps: Int = 0,
    val elapsedSeconds: Int,
    val stateText: String,
    val nextActionHint: String,
    val guidanceMessage: String?,
    val confidenceAverage: Float,
    val estimated: Boolean,
    val auraIntensity: Float = 1f,
    val reducedEffects: Boolean = false,
    val ghostEnabled: Boolean = false,
    /** 0..1 match against the Ghost Trainer guide, shown when the in workout ghost is enabled. */
    val similarity: Float = 0f,
    /** Short confirmation of the gesture that just fired, shown briefly during the cooldown. */
    val gestureFeedback: String? = null,
)

/**
 * Owns the camera workout session state machine: countdown, tracking, guidance on tracking loss,
 * pause and resume, and the session timer (appflow.md section 6). Pose frames flow in through
 * [onPoseFrame]. Rep counting, gestures, Aura, motion recording, and saving are layered on in later
 * phases at the marked extension points.
 */
class WorkoutViewModel(
    private val repository: HoloRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // Exercise and target can change in place when the user switches exercises mid session, so the
    // camera, ViewModel, and gesture detector are never torn down (which would drop the debounce).
    private var exercise: ExerciseType =
        ExerciseType.fromId(savedStateHandle[HoloDestinations.ARG_EXERCISE_TYPE])

    private var targetReps: Int = run {
        val requested = savedStateHandle.get<Int>(HoloDestinations.ARG_TARGET) ?: -1
        if (requested > 0) requested else exercise.defaultTargetReps
    }

    private val _uiState = MutableStateFlow(
        WorkoutUiState(
            exercise = exercise,
            phase = WorkoutPhase.COUNTDOWN,
            countdown = COUNTDOWN_SECONDS,
            reps = 0,
            targetReps = targetReps,
            elapsedSeconds = 0,
            stateText = "Get ready",
            nextActionHint = nextActionHint(exercise),
            guidanceMessage = null,
            confidenceAverage = 0f,
            estimated = exercise.approximate,
        ),
    )
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private val _poseFrame = MutableStateFlow(PoseFrame.EMPTY)
    val poseFrame: StateFlow<PoseFrame> = _poseFrame.asStateFlow()

    private var timerJob: Job? = null
    private var feedbackJob: Job? = null
    private var finished = false

    /** One exercise segment performed within this session (schema supports many sets per session). */
    private data class PerformedExercise(
        val exercise: ExerciseType,
        val reps: Int,
        val confidenceAverage: Float,
        val durationSeconds: Int,
        val targetReps: Int,
    )

    private val performedSets = mutableListOf<PerformedExercise>()
    private var accumulatedReps = 0
    private var segmentStartElapsed = 0

    /** The active rep counter for the current exercise. Exposed for the summary save. */
    var counter = RepCounterFactory.create(exercise)
        private set

    private val gestureDetector = GestureDetector()
    private val recorder = MotionRecorder()

    private val _gestures = Channel<WorkoutGesture>(Channel.BUFFERED)
    /** Gesture driven navigation actions the screen should perform (finish, next, previous). */
    val gestures: Flow<WorkoutGesture> = _gestures.receiveAsFlow()

    // In workout Ghost Trainer overlay guide.
    private var ghostFrames: List<MotionFrame> = emptyList()
    private var ghostPlayhead = 0L
    private val _ghostFrame = MutableStateFlow<Map<String, Offset>>(emptyMap())
    val ghostFrame: StateFlow<Map<String, Offset>> = _ghostFrame.asStateFlow()

    init {
        observeSettings()
        startCountdown()
        startTimer()
        startGhostPlayback()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            repository.settings.collect { settings ->
                val s = settings ?: Settings()
                _uiState.update {
                    it.copy(
                        auraIntensity = s.auraIntensity,
                        reducedEffects = s.reducedEffectsEnabled,
                        ghostEnabled = s.ghostTrainerEnabled,
                    )
                }
                if (s.ghostTrainerEnabled && ghostFrames.isEmpty()) {
                    ghostFrames = repository.loadReplaySource(exercise.id).frames
                }
            }
        }
    }

    private fun startGhostPlayback() {
        viewModelScope.launch {
            while (isActive) {
                delay(GHOST_TICK_MS)
                val phase = _uiState.value.phase
                if (phase == WorkoutPhase.TRACKING && ghostFrames.isNotEmpty()) {
                    ghostPlayhead += GHOST_TICK_MS
                    _ghostFrame.value = GhostReplay.frameAt(ghostFrames, ghostPlayhead)
                }
            }
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            for (remaining in COUNTDOWN_SECONDS downTo 1) {
                _uiState.update { it.copy(phase = WorkoutPhase.COUNTDOWN, countdown = remaining) }
                delay(1000)
            }
            _uiState.update {
                it.copy(phase = WorkoutPhase.TRACKING, countdown = 0, stateText = "Tracking")
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val phase = _uiState.value.phase
                if (phase == WorkoutPhase.TRACKING || phase == WorkoutPhase.GUIDANCE) {
                    _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    /**
     * Receives a pose frame from the camera analyzer (background thread). Applies the confidence
     * gate: low confidence or an out of frame body switches to guidance and suspends counting; good
     * confidence resumes tracking. Rep counting and gesture handling attach here in later phases.
     */
    fun onPoseFrame(frame: PoseFrame) {
        _poseFrame.value = frame
        val current = _uiState.value
        if (current.phase == WorkoutPhase.COUNTDOWN || current.phase == WorkoutPhase.COMPLETE) return

        // Touchless gestures are available while tracking, in guidance, and while paused (to resume).
        gestureDetector.onFrame(frame)?.let(::dispatchGesture)

        // Counting and the confidence gate only run while actively tracking.
        if (current.phase == WorkoutPhase.PAUSED) return

        val confidence = frame.averageLikelihood
        val tracked = !frame.isEmpty && confidence >= PoseConfidence.TRACKING_GATE

        if (!tracked) {
            _uiState.update {
                it.copy(
                    phase = WorkoutPhase.GUIDANCE,
                    guidanceMessage = "Step back so your whole body is in frame, in good light.",
                    confidenceAverage = confidence,
                    stateText = "Repositioning",
                )
            }
            return
        }

        // Tracking is good: feed the counter, record the path, and reflect state.
        counter.update(frame)
        recorder.maybeSample(frame)
        val estimated = exercise.approximate &&
            counter.confidenceAverage < PoseConfidence.ESTIMATED_THRESHOLD
        val ghostNow = _ghostFrame.value
        val similarity = if (_uiState.value.ghostEnabled && ghostNow.isNotEmpty()) {
            GhostReplay.similarity(ghostNow, frame.toGhostOffsets())
        } else {
            0f
        }
        _uiState.update {
            it.copy(
                phase = WorkoutPhase.TRACKING,
                guidanceMessage = null,
                confidenceAverage = confidence,
                reps = counter.reps,
                stateText = counter.stateLabel,
                estimated = estimated,
                similarity = similarity,
            )
        }
    }

    /** The recorded, downsampled motion frames for saving on session end (Phase 7). */
    fun recordedFrames(): List<MotionFrame> = recorder.build()

    /**
     * Maps a detected gesture to its action. Both hands toggles pause, a swipe switches exercise in
     * place (keeping the same detector so its debounce prevents an immediate re-trigger), and a hand
     * raise finishes the session, which needs navigation and is sent to the screen.
     */
    private fun dispatchGesture(gesture: GestureType) {
        when (gesture) {
            GestureType.BOTH_HANDS_HOLD -> {
                togglePause()
                val paused = _uiState.value.phase == WorkoutPhase.PAUSED
                showGestureFeedback(if (paused) "Paused" else "Resumed")
            }
            GestureType.HAND_RAISE -> {
                showGestureFeedback("Finishing")
                _gestures.trySend(WorkoutGesture.FINISH)
            }
            GestureType.SWIPE_RIGHT -> {
                switchToNext()
                showGestureFeedback("Next: ${exercise.displayName}")
            }
            GestureType.SWIPE_LEFT -> {
                switchToPrevious()
                showGestureFeedback("Previous: ${exercise.displayName}")
            }
        }
    }

    /** Briefly surfaces a confirmation that a gesture fired, then clears it during the cooldown. */
    private fun showGestureFeedback(text: String) {
        feedbackJob?.cancel()
        _uiState.update { it.copy(gestureFeedback = text) }
        feedbackJob = viewModelScope.launch {
            delay(GESTURE_FEEDBACK_MS)
            _uiState.update { it.copy(gestureFeedback = null) }
        }
    }

    fun switchToNext() = switchExercise(exercise.next())
    fun switchToPrevious() = switchExercise(exercise.previous())

    /**
     * Switches the tracked exercise without leaving the screen: swaps the counter, resets the rep
     * count and motion recording, reloads the ghost guide, and resumes tracking immediately. The
     * camera and gesture detector are untouched so tracking stays live and the swipe debounce holds.
     */
    private fun switchExercise(target: ExerciseType) {
        if (finished || target == exercise) return
        // Preserve the reps done under the current exercise before switching, so the session
        // remembers everything performed (the on screen counter still resets for the new exercise).
        recordCurrentSegment()

        exercise = target
        targetReps = target.defaultTargetReps
        counter = RepCounterFactory.create(target)
        recorder.reset()
        ghostPlayhead = 0L
        ghostFrames = emptyList()
        _ghostFrame.value = emptyMap()
        viewModelScope.launch {
            if (_uiState.value.ghostEnabled) {
                ghostFrames = repository.loadReplaySource(target.id).frames
            }
        }
        _uiState.update {
            it.copy(
                exercise = target,
                phase = if (it.phase == WorkoutPhase.PAUSED) WorkoutPhase.PAUSED else WorkoutPhase.TRACKING,
                reps = 0,
                targetReps = target.defaultTargetReps,
                previousReps = accumulatedReps,
                stateText = "Switched to ${target.displayName}",
                nextActionHint = nextActionHint(target),
                guidanceMessage = null,
                estimated = target.approximate,
                similarity = 0f,
            )
        }
    }

    /** Records the current exercise segment (only if reps were counted) and advances the timing. */
    private fun recordCurrentSegment() {
        val elapsed = _uiState.value.elapsedSeconds
        if (counter.reps > 0) {
            performedSets.add(
                PerformedExercise(
                    exercise = exercise,
                    reps = counter.reps,
                    confidenceAverage = counter.confidenceAverage,
                    durationSeconds = (elapsed - segmentStartElapsed).coerceAtLeast(0),
                    targetReps = targetReps,
                ),
            )
            accumulatedReps += counter.reps
        }
        segmentStartElapsed = elapsed
    }

    /** Touch fallback for the both hands hold pause gesture (rules.md section 4.5). */
    fun togglePause() {
        _uiState.update {
            when (it.phase) {
                WorkoutPhase.TRACKING, WorkoutPhase.GUIDANCE ->
                    it.copy(phase = WorkoutPhase.PAUSED, stateText = "Paused", guidanceMessage = null)
                WorkoutPhase.PAUSED ->
                    it.copy(phase = WorkoutPhase.TRACKING, stateText = "Tracking")
                else -> it
            }
        }
    }

    /** Persists the resolved camera permission state so Settings can reflect it. */
    fun setPermissionState(state: String) {
        viewModelScope.launch { repository.setCameraPermissionState(state) }
    }

    /**
     * Ends the session: builds the WorkoutSession, ExerciseSet, and downsampled MotionPath, saves
     * them and updates stats and streak, then reports the new session id for navigation to the
     * summary (appflow.md section 7).
     */
    fun stop(onSaved: (sessionId: String) -> Unit) {
        if (finished) return
        finished = true
        timerJob?.cancel()
        _uiState.update { it.copy(phase = WorkoutPhase.COMPLETE, stateText = "Complete") }

        viewModelScope.launch {
            val snapshot = _uiState.value
            // Finalize the current exercise segment. If nothing was counted all session, still
            // record the current exercise so the session saves a row.
            recordCurrentSegment()
            val segments = performedSets.ifEmpty {
                listOf(
                    PerformedExercise(exercise, counter.reps, counter.confidenceAverage, snapshot.elapsedSeconds, targetReps),
                )
            }

            val sessionId = UUID.randomUUID().toString()
            val totalReps = segments.sumOf { it.reps }
            val totalCalories = segments.sumOf {
                CalorieEstimator.estimate(it.exercise.metFactor, it.durationSeconds, it.reps)
            }
            // The session is tagged with the exercise that contributed the most reps.
            val primary = segments.maxByOrNull { it.reps }?.exercise ?: exercise
            val completedStatus =
                if (totalReps > 0 && segments.any { it.reps >= it.targetReps }) WorkoutSession.STATUS_COMPLETED
                else WorkoutSession.STATUS_STOPPED_EARLY

            val session = WorkoutSession(
                id = sessionId,
                date = TimeFormat.todayIso(),
                duration = snapshot.elapsedSeconds,
                totalReps = totalReps,
                exerciseType = primary.id,
                caloriesEstimate = totalCalories,
                completedStatus = completedStatus,
            )
            val exerciseSets = segments.map { segment ->
                ExerciseSet(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    exerciseName = segment.exercise.displayName,
                    targetReps = segment.targetReps,
                    countedReps = segment.reps,
                    confidenceAverage = segment.confidenceAverage,
                )
            }
            repository.saveSession(
                session = session,
                exerciseSets = exerciseSets,
                motionFrames = recorder.build(),
                replayLabel = GHOST_REPLAY_LABEL,
            )
            onSaved(sessionId)
        }
    }

    private fun nextActionHint(exercise: ExerciseType): String = when (exercise) {
        ExerciseType.SQUATS -> "Lower into a squat, then stand tall"
        ExerciseType.JUMPING_JACKS -> "Jump arms and legs out, then back in"
        ExerciseType.PUSHUP_APPROXIMATION -> "Bend elbows to lower, then press up"
    }

    companion object {
        private const val COUNTDOWN_SECONDS = 5
        private const val GHOST_TICK_MS = 40L
        private const val GESTURE_FEEDBACK_MS = 2200L
        private const val GHOST_REPLAY_LABEL =
            "Guidance only. A movement guide, not medical or professional fitness correction."

        val Factory = viewModelFactory {
            initializer { WorkoutViewModel(this.repository(), createSavedStateHandle()) }
        }
    }
}
