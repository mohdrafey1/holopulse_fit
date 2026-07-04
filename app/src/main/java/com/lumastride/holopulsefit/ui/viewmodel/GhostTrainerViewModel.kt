package com.lumastride.holopulsefit.ui.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lumastride.holopulsefit.data.ExerciseType
import com.lumastride.holopulsefit.data.HoloRepository
import com.lumastride.holopulsefit.ghost.GhostReplay
import com.lumastride.holopulsefit.ghost.MotionFrame
import com.lumastride.holopulsefit.navigation.HoloDestinations
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** State for the standalone Ghost Trainer replay screen. */
data class GhostReplayUi(
    val exercise: ExerciseType,
    val frame: Map<String, Offset>,
    val label: String,
    val fromSample: Boolean,
    val playing: Boolean,
    val progress: Float,
    val loaded: Boolean,
)

/**
 * Drives the Ghost Trainer replay: loads the saved motion path for the exercise, or the bundled
 * sample when none exists (appflow.md section 9.2), and plays it back as a looping, interpolated
 * skeleton synchronized to its timestamps (TRD section 7).
 */
class GhostTrainerViewModel(
    repository: HoloRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val exercise: ExerciseType =
        ExerciseType.fromId(savedStateHandle[HoloDestinations.ARG_EXERCISE_TYPE])

    private var frames: List<MotionFrame> = emptyList()
    private var durationMs: Long = 1L
    private var playing = true
    private var playhead = 0L

    private val _ui = MutableStateFlow(
        GhostReplayUi(
            exercise = exercise,
            frame = emptyMap(),
            label = "",
            fromSample = false,
            playing = true,
            progress = 0f,
            loaded = false,
        ),
    )
    val ui: StateFlow<GhostReplayUi> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            val source = repository.loadReplaySource(exercise.id)
            frames = source.frames
            durationMs = GhostReplay.durationMs(frames)
            _ui.update {
                it.copy(label = source.replayLabel, fromSample = source.fromSample, loaded = true)
            }
        }
        viewModelScope.launch {
            while (isActive) {
                delay(TICK_MS)
                if (playing && frames.isNotEmpty()) {
                    playhead += TICK_MS
                    _ui.update {
                        it.copy(
                            frame = GhostReplay.frameAt(frames, playhead),
                            progress = ((playhead % durationMs).toFloat() / durationMs).coerceIn(0f, 1f),
                            playing = true,
                        )
                    }
                }
            }
        }
    }

    fun togglePlay() {
        playing = !playing
        _ui.update { it.copy(playing = playing) }
    }

    companion object {
        private const val TICK_MS = 40L

        val Factory = viewModelFactory {
            initializer { GhostTrainerViewModel(this.repository(), createSavedStateHandle()) }
        }
    }
}
