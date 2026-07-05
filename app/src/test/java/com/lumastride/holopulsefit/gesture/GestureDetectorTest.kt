package com.lumastride.holopulsefit.gesture

import com.lumastride.holopulsefit.pose.Landmark
import com.lumastride.holopulsefit.pose.PoseFrame
import com.lumastride.holopulsefit.pose.PoseLandmarkType.LEFT_SHOULDER
import com.lumastride.holopulsefit.pose.PoseLandmarkType.LEFT_WRIST
import com.lumastride.holopulsefit.pose.PoseLandmarkType.RIGHT_SHOULDER
import com.lumastride.holopulsefit.pose.PoseLandmarkType.RIGHT_WRIST
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private fun gestureFrame(
    t: Long,
    leftWristY: Float,
    rightWristY: Float,
    rightWristX: Float = 0.65f,
    likelihood: Float = 0.9f,
): PoseFrame {
    val map = mapOf(
        LEFT_SHOULDER to Landmark(0.40f, 0.30f, likelihood),
        RIGHT_SHOULDER to Landmark(0.60f, 0.30f, likelihood),
        LEFT_WRIST to Landmark(0.35f, leftWristY, likelihood),
        RIGHT_WRIST to Landmark(rightWristX, rightWristY, likelihood),
    )
    return PoseFrame(map, t)
}

private fun collect(detector: GestureDetector, frames: List<PoseFrame>) =
    frames.mapNotNull { detector.onFrame(it) }

class GestureDetectorTest {

    @Test
    fun handRaiseFiresOnceAfterStableHold() {
        val detector = GestureDetector()
        // Left hand raised (y above shoulder), right hand down, held across 900 ms.
        val frames = (0..9).map { i ->
            gestureFrame(t = i * 100L, leftWristY = 0.10f, rightWristY = 0.50f)
        }
        val events = collect(detector, frames)
        assertEquals(listOf(GestureType.HAND_RAISE), events)
    }

    @Test
    fun singleFrameDoesNotFire() {
        val detector = GestureDetector()
        val events = collect(detector, listOf(gestureFrame(0L, leftWristY = 0.10f, rightWristY = 0.50f)))
        assertTrue(events.isEmpty())
    }

    @Test
    fun bothHandsTogglesOncePerHoldCycle() {
        val detector = GestureDetector()
        val frames = mutableListOf<PoseFrame>()
        // Hold both hands up from 0 to 1200 ms (first fire around 1000 ms).
        for (t in 0..1200 step 100) frames += gestureFrame(t.toLong(), 0.10f, 0.10f)
        // Release (hands down) 1300 to 1500.
        for (t in 1300..1500 step 100) frames += gestureFrame(t.toLong(), 0.50f, 0.50f)
        // Hold both hands again after the cooldown, from 4600 to 5800 ms (second fire around 5600).
        for (t in 4600..5800 step 100) frames += gestureFrame(t.toLong(), 0.10f, 0.10f)

        val events = collect(detector, frames)
        assertEquals(listOf(GestureType.BOTH_HANDS_HOLD, GestureType.BOTH_HANDS_HOLD), events)
    }

    @Test
    fun secondGestureWithinCooldownIsBlocked() {
        val detector = GestureDetector()
        val frames = mutableListOf<PoseFrame>()
        // First hold fires around 1000 ms.
        for (t in 0..1200 step 100) frames += gestureFrame(t.toLong(), 0.10f, 0.10f)
        for (t in 1300..1500 step 100) frames += gestureFrame(t.toLong(), 0.50f, 0.50f)
        // Second hold completes at ~2600 ms, within the 3 second cooldown, so it must not fire.
        for (t in 1600..2700 step 100) frames += gestureFrame(t.toLong(), 0.10f, 0.10f)

        val events = collect(detector, frames)
        assertEquals(listOf(GestureType.BOTH_HANDS_HOLD), events)
    }

    @Test
    fun swipeRightDetectedFromConsistentMovement() {
        val detector = GestureDetector()
        // Hands down; right wrist moves left to right across the frame.
        val frames = listOf(
            gestureFrame(0L, 0.50f, 0.50f, rightWristX = 0.40f),
            gestureFrame(120L, 0.50f, 0.50f, rightWristX = 0.50f),
            gestureFrame(240L, 0.50f, 0.50f, rightWristX = 0.60f),
            gestureFrame(360L, 0.50f, 0.50f, rightWristX = 0.68f),
        )
        val events = collect(detector, frames)
        assertEquals(listOf(GestureType.SWIPE_RIGHT), events)
    }

    @Test
    fun lowConfidenceProducesNoGesture() {
        val detector = GestureDetector()
        val frames = (0..12).map { i ->
            gestureFrame(t = i * 100L, leftWristY = 0.10f, rightWristY = 0.10f, likelihood = 0.3f)
        }
        assertTrue(collect(detector, frames).isEmpty())
    }

    @Test
    fun lowHandHorizontalMovementDoesNotSwipe() {
        val detector = GestureDetector()
        // Hands low near the hips (like a squat), sweeping sideways: out of the shoulder height band.
        val frames = listOf(
            gestureFrame(0L, 0.60f, 0.60f, rightWristX = 0.40f),
            gestureFrame(120L, 0.60f, 0.60f, rightWristX = 0.52f),
            gestureFrame(240L, 0.60f, 0.60f, rightWristX = 0.62f),
            gestureFrame(360L, 0.60f, 0.60f, rightWristX = 0.70f),
        )
        assertTrue(collect(detector, frames).isEmpty())
    }

    @Test
    fun verticalDominantMovementDoesNotSwipe() {
        val detector = GestureDetector()
        // Diagonal motion where the vertical travel dominates: not a deliberate horizontal sweep.
        val frames = listOf(
            gestureFrame(0L, 0.50f, 0.30f, rightWristX = 0.44f),
            gestureFrame(120L, 0.50f, 0.37f, rightWristX = 0.53f),
            gestureFrame(240L, 0.50f, 0.44f, rightWristX = 0.60f),
            gestureFrame(360L, 0.50f, 0.50f, rightWristX = 0.68f),
        )
        assertTrue(collect(detector, frames).isEmpty())
    }

    @Test
    fun jitteryWristDoesNotSwipe() {
        val detector = GestureDetector()
        // Small back and forth movement below the swipe threshold.
        val frames = (0..6).map { i ->
            val x = 0.60f + if (i % 2 == 0) 0.02f else -0.02f
            gestureFrame(t = i * 100L, leftWristY = 0.50f, rightWristY = 0.50f, rightWristX = x)
        }
        assertTrue(collect(detector, frames).isEmpty())
    }
}
