package com.lumastride.holopulsefit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lumastride.holopulsefit.data.HoloRepository
import com.lumastride.holopulsefit.ui.HistoryUi
import com.lumastride.holopulsefit.ui.toSessionUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** History list plus the progress summary strip, from live Room data (appflow.md section 8). */
class HistoryViewModel(private val repository: HoloRepository) : ViewModel() {

    val state: StateFlow<HistoryUi> = combine(
        repository.allSessions,
        repository.userStats,
        repository.sessionIdsWithPaths,
    ) { sessions, stats, pathIds ->
        val pathSet = pathIds.toSet()
        HistoryUi(
            totalSessions = stats?.totalSessions ?: sessions.size,
            totalCalories = stats?.totalCalories ?: sessions.sumOf { it.caloriesEstimate },
            bestStreak = stats?.bestStreak ?: 0,
            sessions = sessions.map { it.toSessionUi(it.id in pathSet) },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUi(0, 0, 0, emptyList()),
    )

    companion object {
        val Factory = viewModelFactory {
            initializer { HistoryViewModel(this.repository()) }
        }
    }
}
