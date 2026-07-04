package com.lumastride.holopulsefit.counting

import com.lumastride.holopulsefit.pose.Landmark
import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.pose.PoseLandmarkType
import com.lumastride.holopulsefit.pose.PoseLandmarkType.LEFT_ELBOW
import com.lumastride.holopulsefit.pose.PoseLandmarkType.LEFT_HIP
import com.lumastride.holopulsefit.pose.PoseLandmarkType.LEFT_KNEE
import com.lumastride.holopulsefit.pose.PoseLandmarkType.LEFT_SHOULDER
import com.lumastride.holopulsefit.pose.PoseLandmarkType.LEFT_WRIST
import com.lumastride.holopulsefit.pose.PoseLandmarkType.RIGHT_ELBOW
import com.lumastride.holopulsefit.pose.PoseLandmarkType.RIGHT_HIP
import com.lumastride.holopulsefit.pose.PoseLandmarkType.RIGHT_KNEE
import com.lumastride.holopulsefit.pose.PoseLandmarkType.RIGHT_SHOULDER
import com.lumastride.holopulsefit.pose.PoseLandmarkType.RIGHT_WRIST
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Builds a synthetic pose frame from landmark positions with a uniform likelihood. */
private fun frame(
    likelihood: Float = 0.9f,
    vararg entries: Pair<PoseLandmarkType, Pair<Float, Float>>,
): PoseFrame = PoseFrame(
    landmarks = entries.associate { (type, p) -> type to Landmark(p.first, p.second, likelihood) },
    timestampMs = 0L,
)

private fun squatStand(l: Float = 0.9f) = frame(
    l,
    LEFT_SHOULDER to (0.42f to 0.20f), RIGHT_SHOULDER to (0.58f to 0.20f),
    LEFT_HIP to (0.45f to 0.50f), RIGHT_HIP to (0.55f to 0.50f),
    LEFT_KNEE to (0.44f to 0.80f), RIGHT_KNEE to (0.56f to 0.80f),
)

private fun squatDown(l: Float = 0.9f) = frame(
    l,
    LEFT_SHOULDER to (0.42f to 0.25f), RIGHT_SHOULDER to (0.58f to 0.25f),
    LEFT_HIP to (0.45f to 0.74f), RIGHT_HIP to (0.55f to 0.74f),
    LEFT_KNEE to (0.44f to 0.82f), RIGHT_KNEE to (0.56f to 0.82f),
)

private fun squatHalf(l: Float = 0.9f) = frame(
    l,
    LEFT_SHOULDER to (0.42f to 0.20f), RIGHT_SHOULDER to (0.58f to 0.20f),
    LEFT_HIP to (0.45f to 0.62f), RIGHT_HIP to (0.55f to 0.62f),
    LEFT_KNEE to (0.44f to 0.80f), RIGHT_KNEE to (0.56f to 0.80f),
)

private fun jackClosed(l: Float = 0.9f) = frame(
    l,
    LEFT_SHOULDER to (0.42f to 0.25f), RIGHT_SHOULDER to (0.58f to 0.25f),
    LEFT_WRIST to (0.30f to 0.50f), RIGHT_WRIST to (0.70f to 0.50f),
    LEFT_HIP to (0.45f to 0.55f), RIGHT_HIP to (0.55f to 0.55f),
)

private fun jackOpen(l: Float = 0.9f) = frame(
    l,
    LEFT_SHOULDER to (0.42f to 0.25f), RIGHT_SHOULDER to (0.58f to 0.25f),
    LEFT_WRIST to (0.25f to 0.05f), RIGHT_WRIST to (0.75f to 0.05f),
    LEFT_HIP to (0.45f to 0.55f), RIGHT_HIP to (0.55f to 0.55f),
)

private fun pushUp(l: Float = 0.9f) = frame(
    l,
    LEFT_SHOULDER to (0.40f to 0.30f), RIGHT_SHOULDER to (0.60f to 0.30f),
    LEFT_ELBOW to (0.40f to 0.50f), RIGHT_ELBOW to (0.60f to 0.50f),
    LEFT_WRIST to (0.40f to 0.70f), RIGHT_WRIST to (0.60f to 0.70f),
)

private fun pushDown(l: Float = 0.9f) = frame(
    l,
    LEFT_SHOULDER to (0.40f to 0.30f), RIGHT_SHOULDER to (0.60f to 0.30f),
    LEFT_ELBOW to (0.40f to 0.50f), RIGHT_ELBOW to (0.60f to 0.50f),
    LEFT_WRIST to (0.55f to 0.50f), RIGHT_WRIST to (0.45f to 0.50f),
)

private fun feed(counter: PhaseRepCounter, frames: List<PoseFrame>) {
    frames.forEach { counter.update(it) }
}

class RepCountersTest {

    @Test
    fun squatCountsThreeCompleteCycles() {
        val counter = SquatCounter()
        val frames = mutableListOf(squatStand())
        repeat(3) {
            frames += squatDown()
            frames += squatStand()
        }
        feed(counter, frames)
        assertEquals(3, counter.reps)
    }

    @Test
    fun squatHalfRepsDoNotCount() {
        val counter = SquatCounter()
        val frames = mutableListOf(squatStand())
        repeat(4) {
            frames += squatHalf() // never reaches the down enter threshold
            frames += squatStand()
        }
        feed(counter, frames)
        assertEquals(0, counter.reps)
    }

    @Test
    fun squatLowConfidenceIsGated() {
        val counter = SquatCounter()
        // The down frames are below the confidence gate, so no rep is counted.
        feed(counter, listOf(squatStand(), squatDown(0.3f), squatStand(), squatDown(0.3f), squatStand()))
        assertEquals(0, counter.reps)
    }

    @Test
    fun squatIncompleteDownWithoutUpDoesNotCount() {
        val counter = SquatCounter()
        feed(counter, listOf(squatStand(), squatDown()))
        assertEquals(0, counter.reps)
    }

    @Test
    fun jumpingJackCountsCycles() {
        val counter = JumpingJackCounter()
        val frames = mutableListOf(jackClosed())
        repeat(5) {
            frames += jackOpen()
            frames += jackClosed()
        }
        feed(counter, frames)
        assertEquals(5, counter.reps)
    }

    @Test
    fun pushUpCountsCycles() {
        val counter = PushUpCounter()
        val frames = mutableListOf(pushUp())
        repeat(4) {
            frames += pushDown()
            frames += pushUp()
        }
        feed(counter, frames)
        assertEquals(4, counter.reps)
    }

    @Test
    fun confidenceAverageReflectsInputLikelihood() {
        val counter = SquatCounter()
        feed(counter, listOf(squatStand(0.7f), squatDown(0.7f), squatStand(0.7f)))
        assertEquals(1, counter.reps)
        assertTrue("confidence avg was ${counter.confidenceAverage}", counter.confidenceAverage in 0.6f..0.8f)
    }

    @Test
    fun resetClearsState() {
        val counter = SquatCounter()
        feed(counter, listOf(squatStand(), squatDown(), squatStand()))
        assertEquals(1, counter.reps)
        counter.reset()
        assertEquals(0, counter.reps)
        assertEquals("Ready", counter.stateLabel)
    }
}
