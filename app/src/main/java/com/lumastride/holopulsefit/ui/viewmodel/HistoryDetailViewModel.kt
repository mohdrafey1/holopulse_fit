package com.lumastride.holopulsefit.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lumastride.holopulsefit.data.HoloRepository
import com.lumastride.holopulsefit.navigation.HoloDestinations
import com.lumastride.holopulsefit.ui.SessionUi
import com.lumastride.holopulsefit.ui.toSessionUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Loads one session for the History Detail screen and handles its deletion. */
class HistoryDetailViewModel(
    private val repository: HoloRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val sessionId: String =
        checkNotNull(savedStateHandle[HoloDestinations.ARG_SESSION_ID])

    private val _state = MutableStateFlow<SessionUi?>(null)
    val state: StateFlow<SessionUi?> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val detail = repository.getSessionDetail(sessionId)
            _state.value = detail?.session?.toSessionUi(detail.hasMotionPath)
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            onDeleted()
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { HistoryDetailViewModel(this.repository(), createSavedStateHandle()) }
        }
    }
}
