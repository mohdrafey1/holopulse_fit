package com.lumastride.holopulsefit.data

import android.content.Context
import kotlinx.serialization.json.Json

/**
 * Reads the bundled sample data JSON from assets (schema.md section 6). Used to seed the database
 * on first run and to provide the Ghost Trainer fallback source. Never writes anything.
 */
class SampleDataLoader(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    fun loadWorkoutHistory(): List<SampleWorkoutDto> =
        readAsset("sample_data/sample-workout-history.json")?.let {
            runCatching { json.decodeFromString<List<SampleWorkoutDto>>(it) }.getOrNull()
        } ?: emptyList()

    fun loadMotionPath(): SampleMotionPathDto? =
        readAsset("sample_data/sample-motion-paths.json")?.let {
            runCatching { json.decodeFromString<SampleMotionPathDto>(it) }.getOrNull()
        }

    /** Raw contents of ghost-trainer-sample.json, loaded for its label and presence check. */
    fun loadGhostSampleRaw(): String? = readAsset("sample_data/ghost-trainer-sample.json")

    private fun readAsset(path: String): String? = runCatching {
        context.assets.open(path).bufferedReader().use { it.readText() }
    }.getOrNull()
}
