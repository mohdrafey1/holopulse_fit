package com.lumastride.holopulsefit.counting

import com.lumastride.holopulsefit.data.ExerciseType

/** Creates the rep counter engine for an exercise type. */
object RepCounterFactory {
    fun create(exercise: ExerciseType): PhaseRepCounter = when (exercise) {
        ExerciseType.SQUATS -> SquatCounter()
        ExerciseType.JUMPING_JACKS -> JumpingJackCounter()
        ExerciseType.PUSHUP_APPROXIMATION -> PushUpCounter()
    }
}
