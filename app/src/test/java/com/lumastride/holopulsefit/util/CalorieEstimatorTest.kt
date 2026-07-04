package com.lumastride.holopulsefit.util

import com.lumastride.holopulsefit.data.ExerciseType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalorieEstimatorTest {

    @Test
    fun squatSetLandsNearSampleValue() {
        // Sample: squats, 180 seconds, 20 reps -> around 35 kcal.
        val kcal = CalorieEstimator.estimate(ExerciseType.SQUATS.metFactor, 180, 20)
        assertTrue("expected near 35 but was $kcal", kcal in 30..40)
    }

    @Test
    fun jumpingJackSetLandsNearSampleValue() {
        // Sample: jumping jacks, 120 seconds, 30 reps -> around 40 kcal.
        val kcal = CalorieEstimator.estimate(ExerciseType.JUMPING_JACKS.metFactor, 120, 30)
        assertTrue("expected near 40 but was $kcal", kcal in 35..45)
    }

    @Test
    fun moreRepsAndDurationYieldMoreCalories() {
        val low = CalorieEstimator.estimate(ExerciseType.SQUATS.metFactor, 60, 5)
        val high = CalorieEstimator.estimate(ExerciseType.SQUATS.metFactor, 300, 40)
        assertTrue(high > low)
    }

    @Test
    fun zeroInputsNeverNegative() {
        assertEquals(0, CalorieEstimator.estimate(ExerciseType.SQUATS.metFactor, 0, 0))
        assertEquals(0, CalorieEstimator.estimate(ExerciseType.PUSHUP_APPROXIMATION.metFactor, -10, -5))
    }
}
