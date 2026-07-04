package com.lumastride.holopulsefit.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    private val today = LocalDate.of(2026, 7, 3)

    @Test
    fun firstEverSessionStartsStreakAtOne() {
        val result = StreakCalculator.update(lastWorkoutDate = null, today = today, currentStreak = 0, bestStreak = 0)
        assertEquals(1, result.currentStreak)
        assertEquals(1, result.bestStreak)
    }

    @Test
    fun consecutiveDayIncrementsStreak() {
        val result = StreakCalculator.update(
            lastWorkoutDate = today.minusDays(1),
            today = today,
            currentStreak = 3,
            bestStreak = 5,
        )
        assertEquals(4, result.currentStreak)
        assertEquals(5, result.bestStreak)
    }

    @Test
    fun consecutiveDayCanSetNewBest() {
        val result = StreakCalculator.update(
            lastWorkoutDate = today.minusDays(1),
            today = today,
            currentStreak = 5,
            bestStreak = 5,
        )
        assertEquals(6, result.currentStreak)
        assertEquals(6, result.bestStreak)
    }

    @Test
    fun sameDaySessionKeepsStreak() {
        val result = StreakCalculator.update(
            lastWorkoutDate = today,
            today = today,
            currentStreak = 4,
            bestStreak = 7,
        )
        assertEquals(4, result.currentStreak)
        assertEquals(7, result.bestStreak)
    }

    @Test
    fun gapResetsStreakToOne() {
        val result = StreakCalculator.update(
            lastWorkoutDate = today.minusDays(3),
            today = today,
            currentStreak = 6,
            bestStreak = 6,
        )
        assertEquals(1, result.currentStreak)
        assertEquals(6, result.bestStreak)
    }

    @Test
    fun fromDatesComputesLongestAndCurrentRun() {
        // Two runs: a 3 day run, then a gap, then a 2 day run ending at the latest date.
        val dates = listOf(
            LocalDate.of(2026, 6, 1),
            LocalDate.of(2026, 6, 2),
            LocalDate.of(2026, 6, 3),
            LocalDate.of(2026, 6, 10),
            LocalDate.of(2026, 6, 11),
        )
        val result = StreakCalculator.fromDates(dates)
        assertEquals(2, result.currentStreak)
        assertEquals(3, result.bestStreak)
    }

    @Test
    fun fromDatesIgnoresDuplicateDays() {
        val dates = listOf(
            LocalDate.of(2026, 6, 1),
            LocalDate.of(2026, 6, 1),
            LocalDate.of(2026, 6, 2),
        )
        val result = StreakCalculator.fromDates(dates)
        assertEquals(2, result.currentStreak)
        assertEquals(2, result.bestStreak)
    }

    @Test
    fun fromDatesEmptyIsZero() {
        val result = StreakCalculator.fromDates(emptyList())
        assertEquals(0, result.currentStreak)
        assertEquals(0, result.bestStreak)
    }
}
