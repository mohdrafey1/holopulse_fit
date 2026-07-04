package com.lumastride.holopulsefit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lumastride.holopulsefit.data.HoloRepository
import com.lumastride.holopulsefit.data.entities.Settings
import com.lumastride.holopulsefit.ui.screens.SettingsUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Exposes persisted settings and forwards edits to the repository (appflow.md section 10). */
class SettingsViewModel(private val repository: HoloRepository) : ViewModel() {

    val state: StateFlow<SettingsUi> = repository.settings
        .map { it.toUi() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Settings().toUi(),
        )

    fun setAuraIntensity(value: Float) = viewModelScope.launch { repository.setAuraIntensity(value) }
    fun setReducedEffects(enabled: Boolean) = viewModelScope.launch { repository.setReducedEffects(enabled) }
    fun setGhostTrainerEnabled(enabled: Boolean) = viewModelScope.launch { repository.setGhostTrainerEnabled(enabled) }
    fun clearHistory() = viewModelScope.launch { repository.clearAllHistory() }

    private fun Settings?.toUi(): SettingsUi {
        val s = this ?: Settings()
        return SettingsUi(
            cameraPermissionLabel = permissionLabel(s.cameraPermissionState),
            auraIntensity = s.auraIntensity,
            reducedEffectsEnabled = s.reducedEffectsEnabled,
            ghostTrainerEnabled = s.ghostTrainerEnabled,
        )
    }

    private fun permissionLabel(state: String): String = when (state) {
        Settings.PERMISSION_GRANTED -> "Camera permission granted."
        Settings.PERMISSION_DENIED -> "Camera permission denied. Grant it to start tracking."
        Settings.PERMISSION_PERMANENTLY_DENIED ->
            "Camera permission is blocked. Enable it in system settings to track workouts."
        else -> "Camera permission is requested at your first workout."
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { SettingsViewModel(this.repository()) }
        }
    }
}
