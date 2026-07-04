package com.lumastride.holopulsefit.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lumastride.holopulsefit.data.ExerciseType
import com.lumastride.holopulsefit.data.HoloRepository
import com.lumastride.holopulsefit.navigation.HoloDestinations
import com.lumastride.holopulsefit.pose.PoseConfidence
import com.lumastride.holopulsefit.ui.screens.SummaryUi
import com.lumastride.holopulsefit.util.TimeFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Loads the just saved session for the Session Summary screen (appflow.md section 7). */
class SummaryViewModel(
    private val repository: HoloRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val sessionId: String =
        checkNotNull(savedStateHandle[HoloDestinations.ARG_SESSION_ID])

    private val _state = MutableStateFlow<SummaryUi?>(null)
    val state: StateFlow<SummaryUi?> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val detail = repository.getSessionDetail(sessionId) ?: return@launch
            val stats = repository.userStatsOnce()
            val exercise = ExerciseType.fromId(detail.session.exerciseType)
            val confidence = detail.exerciseSet?.confidenceAverage ?: 0f
            val estimated = exercise.approximate && confidence < PoseConfidence.ESTIMATED_THRESHOLD
            _state.value = SummaryUi(
                exerciseName = exercise.displayName,
                exerciseTypeId = exercise.id,
                reps = detail.session.totalReps,
                targetReps = detail.exerciseSet?.targetReps ?: detail.session.totalReps,
                durationLabel = TimeFormat.duration(detail.session.duration),
                calories = detail.session.caloriesEstimate,
                streak = stats?.currentStreak ?: 0,
                estimated = estimated,
                saved = true,
                hasMotionPath = detail.hasMotionPath,
            )
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { SummaryViewModel(this.repository(), createSavedStateHandle()) }
        }
    }
}
