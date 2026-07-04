package com.lumastride.holopulsefit.util

import java.time.LocalDate

/**
 * Streak update logic bound to the schema (schema.md section 5.1).
 *
 * On session save: if the last workout was yesterday the current streak increments, if it was today
 * the streak is unchanged, otherwise it resets to 1. The best streak takes the running maximum.
 */
object StreakCalculator {

    data class StreakResult(val currentStreak: Int, val bestStreak: Int)

    /**
     * @param lastWorkoutDate the previously stored last workout date, or null if none.
     * @param today the date of the session being saved.
     * @param currentStreak the previously stored current streak.
     * @param bestStreak the previously stored best streak.
     */
    fun update(
        lastWorkoutDate: LocalDate?,
        today: LocalDate,
        currentStreak: Int,
        bestStreak: Int,
    ): StreakResult {
        val newCurrent = when (lastWorkoutDate) {
            null -> 1
            today -> currentStreak.coerceAtLeast(1)
            today.minusDays(1) -> currentStreak + 1
            else -> 1
        }
        return StreakResult(newCurrent, maxOf(bestStreak, newCurrent))
    }

    /**
     * Computes a streak result from a full set of workout dates, used when seeding or recomputing
     * stats. The current streak is the run of consecutive days ending at the latest date; the best
     * streak is the longest consecutive day run overall.
     */
    fun fromDates(dates: Collection<LocalDate>): StreakResult {
        if (dates.isEmpty()) return StreakResult(0, 0)
        val sorted = dates.toSortedSet().toList()
        var best = 1
        var run = 1
        for (i in 1 until sorted.size) {
            run = if (sorted[i - 1].plusDays(1) == sorted[i]) run + 1 else 1
            best = maxOf(best, run)
        }
        // Current run ends at the latest date.
        var current = 1
        for (i in sorted.size - 1 downTo 1) {
            if (sorted[i - 1].plusDays(1) == sorted[i]) current++ else break
        }
        return StreakResult(current, best)
    }
}
