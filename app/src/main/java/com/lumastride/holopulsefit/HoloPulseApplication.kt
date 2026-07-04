package com.lumastride.holopulsefit

import android.app.Application
import android.content.Context
import com.lumastride.holopulsefit.data.HoloDatabase
import com.lumastride.holopulsefit.data.HoloRepository
import com.lumastride.holopulsefit.data.SampleDataLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Lightweight manual dependency container. Holds the singleton database and repository and seeds
 * sample data once on first run. Avoids a DI framework for a single module app while keeping the
 * repository the single source of truth for ViewModels.
 */
class AppContainer(context: Context) {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val database = HoloDatabase.getInstance(context.applicationContext)

    val repository = HoloRepository(database, SampleDataLoader(context.applicationContext))

    fun initialize() {
        applicationScope.launch { repository.ensureInitialized() }
    }
}

/** Application entry point that builds the [AppContainer]. */
class HoloPulseApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.initialize()
    }
}
