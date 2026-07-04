package com.lumastride.holopulsefit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lumastride.holopulsefit.data.HoloRepository
import com.lumastride.holopulsefit.data.entities.UserStats
import com.lumastride.holopulsefit.data.entities.WorkoutSession
import com.lumastride.holopulsefit.ui.DashboardUi
import com.lumastride.holopulsefit.ui.greetingForHour
import com.lumastride.holopulsefit.ui.toSessionUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime

/**
 * Dashboard state from live Room data: today progress, streak, completed count, and the most recent
 * sessions (appflow.md section 3).
 */
class DashboardViewModel(repository: HoloRepository) : ViewModel() {

    val state: StateFlow<DashboardUi> = combine(
        repository.allSessions,
        repository.userStats,
        repository.sessionIdsWithPaths,
    ) { sessions, stats, pathIds ->
        buildDashboard(sessions, stats, pathIds.toSet())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUi(
            greeting = greetingForHour(LocalTime.now().hour),
            todayProgress = 0f,
            todayGoalLabel = "",
            sessionsCompleted = 0,
            currentStreak = 0,
            recent = emptyList(),
        ),
    )

    private fun buildDashboard(
        sessions: List<WorkoutSession>,
        stats: UserStats?,
        pathIds: Set<String>,
    ): DashboardUi {
        val today = LocalDate.now().toString()
        val todayCompleted = sessions.count { it.date == today }
        val progress = (todayCompleted.toFloat() / DAILY_GOAL).coerceIn(0f, 1f)
        return DashboardUi(
            greeting = greetingForHour(LocalTime.now().hour),
            todayProgress = progress,
            todayGoalLabel = "$todayCompleted of $DAILY_GOAL workouts today",
            sessionsCompleted = todayCompleted,
            currentStreak = stats?.currentStreak ?: 0,
            recent = sessions.take(RECENT_LIMIT).map { it.toSessionUi(it.id in pathIds) },
        )
    }

    companion object {
        private const val DAILY_GOAL = 2
        private const val RECENT_LIMIT = 3

        val Factory = viewModelFactory {
            initializer { DashboardViewModel(this.repository()) }
        }
    }
}
