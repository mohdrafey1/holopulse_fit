package com.lumastride.holopulsefit.ghost

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.lerp
import kotlin.math.hypot

/**
 * Pure playback and comparison helpers for Ghost Trainer replay (TRD section 7). Given a recorded
 * motion series and an elapsed time it produces an interpolated, looping skeleton frame, and it can
 * score how closely a live pose matches the ghost for the similarity cue.
 */
object GhostReplay {

    /** Total loop duration in ms, at least 1 to avoid division by zero. */
    fun durationMs(frames: List<MotionFrame>): Long =
        frames.lastOrNull()?.timestampMs?.coerceAtLeast(1L) ?: 1L

    /**
     * The interpolated skeleton at [elapsedMs], looped over the series duration. Returns normalized
     * joint offsets keyed by joint name, ready for the GhostSkeleton renderer.
     */
    fun frameAt(frames: List<MotionFrame>, elapsedMs: Long): Map<String, Offset> {
        if (frames.isEmpty()) return emptyMap()
        if (frames.size == 1) return frames[0].joints.mapValues { Offset(it.value.x, it.value.y) }

        val duration = durationMs(frames)
        val t = ((elapsedMs % duration) + duration) % duration

        var i = 0
        while (i < frames.size - 1 && frames[i + 1].timestampMs <= t) i++
        val a = frames[i]
        val b = frames[minOf(i + 1, frames.size - 1)]
        val span = (b.timestampMs - a.timestampMs).coerceAtLeast(1L)
        val f = ((t - a.timestampMs).toFloat() / span).coerceIn(0f, 1f)
        return interpolate(a.joints, b.joints, f)
    }

    private fun interpolate(
        a: Map<String, JointXY>,
        b: Map<String, JointXY>,
        f: Float,
    ): Map<String, Offset> {
        val keys = a.keys.intersect(b.keys)
        return keys.associateWith { key ->
            val pa = a.getValue(key)
            val pb = b.getValue(key)
            Offset(lerp(pa.x, pb.x, f), lerp(pa.y, pb.y, f))
        }
    }

    /**
     * A 0..1 similarity score between a ghost frame and a live frame, from the average joint
     * distance over shared joints (1 means overlapping, 0 means far apart). Used for the timing and
     * path similarity cue.
     */
    fun similarity(ghost: Map<String, Offset>, live: Map<String, Offset>): Float {
        val keys = ghost.keys.intersect(live.keys)
        if (keys.isEmpty()) return 0f
        val avgDistance = keys.map { key ->
            val g = ghost.getValue(key)
            val l = live.getValue(key)
            hypot((g.x - l.x).toDouble(), (g.y - l.y).toDouble()).toFloat()
        }.average().toFloat()
        // A 0.5 normalized distance or more reads as no match.
        return (1f - avgDistance * 2f).coerceIn(0f, 1f)
    }
}
