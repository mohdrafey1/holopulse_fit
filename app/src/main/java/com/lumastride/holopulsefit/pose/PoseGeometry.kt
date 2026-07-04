package com.lumastride.holopulsefit.pose

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * Small 2D geometry helpers over [PoseFrame] used by the rep counters. All operate on normalized
 * coordinates and return null when a required landmark is missing, so counters can gate cleanly.
 */
object PoseGeometry {

    fun y(frame: PoseFrame, type: PoseLandmarkType): Float? = frame[type]?.y
    fun x(frame: PoseFrame, type: PoseLandmarkType): Float? = frame[type]?.x

    /** Mean y of the given landmarks, or null if any is missing. */
    fun avgY(frame: PoseFrame, vararg types: PoseLandmarkType): Float? {
        var sum = 0f
        for (t in types) sum += (frame[t]?.y ?: return null)
        return sum / types.size
    }

    /** Mean x of the given landmarks, or null if any is missing. */
    fun avgX(frame: PoseFrame, vararg types: PoseLandmarkType): Float? {
        var sum = 0f
        for (t in types) sum += (frame[t]?.x ?: return null)
        return sum / types.size
    }

    /** Horizontal distance between two landmarks, or null if either is missing. */
    fun horizontalSpread(frame: PoseFrame, a: PoseLandmarkType, b: PoseLandmarkType): Float? {
        val ax = frame[a]?.x ?: return null
        val bx = frame[b]?.x ?: return null
        return abs(ax - bx)
    }

    /** Euclidean distance between two landmarks, or null if either is missing. */
    fun distance(frame: PoseFrame, a: PoseLandmarkType, b: PoseLandmarkType): Float? {
        val la = frame[a] ?: return null
        val lb = frame[b] ?: return null
        return hypot((la.x - lb.x), (la.y - lb.y))
    }

    /**
     * Interior angle in degrees at vertex [b] formed by points a-b-c, or null if any is missing.
     * Used for elbow and knee flexion.
     */
    fun angleDegrees(
        frame: PoseFrame,
        a: PoseLandmarkType,
        b: PoseLandmarkType,
        c: PoseLandmarkType,
    ): Float? {
        val la = frame[a] ?: return null
        val lb = frame[b] ?: return null
        val lc = frame[c] ?: return null
        val v1x = la.x - lb.x
        val v1y = la.y - lb.y
        val v2x = lc.x - lb.x
        val v2y = lc.y - lb.y
        val dot = v1x * v2x + v1y * v2y
        val mag1 = sqrt(v1x * v1x + v1y * v1y)
        val mag2 = sqrt(v2x * v2x + v2y * v2y)
        if (mag1 == 0f || mag2 == 0f) return null
        val cos = (dot / (mag1 * mag2)).coerceIn(-1f, 1f)
        return Math.toDegrees(acos(cos).toDouble()).toFloat()
    }
}
