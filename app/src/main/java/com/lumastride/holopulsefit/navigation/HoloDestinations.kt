package com.lumastride.holopulsefit.navigation

/**
 * Central definition of every navigation route (appflow.md section 1). Routes that carry an
 * argument expose a [build] helper so call sites never hand assemble route strings.
 */
object HoloDestinations {
    const val LAUNCH = "launch"
    const val DASHBOARD = "dashboard"
    const val LIBRARY = "library"
    const val HISTORY = "history"
    const val SETTINGS = "settings"

    const val ARG_EXERCISE_TYPE = "exerciseType"
    const val ARG_SESSION_ID = "sessionId"
    const val ARG_TARGET = "target"

    /** Camera workout session for a given exercise type, with an optional target rep count. */
    object Workout {
        const val ROUTE = "workout/{$ARG_EXERCISE_TYPE}?$ARG_TARGET={$ARG_TARGET}"
        fun build(exerciseTypeId: String, target: Int = -1) = "workout/$exerciseTypeId?$ARG_TARGET=$target"
    }

    /** Session summary shown after a workout ends. */
    object Summary {
        const val ROUTE = "summary/{$ARG_SESSION_ID}"
        fun build(sessionId: String) = "summary/$sessionId"
    }

    /** History detail for a saved session. */
    object HistoryDetail {
        const val ROUTE = "history/{$ARG_SESSION_ID}"
        fun build(sessionId: String) = "history/$sessionId"
    }

    /** Ghost Trainer replay for an exercise type (saved path or bundled sample fallback). */
    object Ghost {
        const val ROUTE = "ghost/{$ARG_EXERCISE_TYPE}"
        fun build(exerciseTypeId: String) = "ghost/$exerciseTypeId"
    }
}
