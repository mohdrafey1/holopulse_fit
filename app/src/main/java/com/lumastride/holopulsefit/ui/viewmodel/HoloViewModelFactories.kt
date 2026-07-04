package com.lumastride.holopulsefit.ui.viewmodel

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.lumastride.holopulsefit.HoloPulseApplication
import com.lumastride.holopulsefit.data.HoloRepository

/** Resolves the shared repository from the application inside a ViewModel factory initializer. */
internal fun CreationExtras.repository(): HoloRepository {
    val app = this[APPLICATION_KEY] as HoloPulseApplication
    return app.container.repository
}
