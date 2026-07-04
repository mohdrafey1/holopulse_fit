package com.lumastride.holopulsefit.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Time and date formatting helpers shared across screens. */
object TimeFormat {

    private val displayDateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)

    /** Formats a duration in seconds as m:ss (or h:mm:ss for long sessions). */
    fun duration(seconds: Int): String {
        val safe = seconds.coerceAtLeast(0)
        val h = safe / 3600
        val m = (safe % 3600) / 60
        val s = safe % 60
        return if (h > 0) {
            String.format(Locale.US, "%d:%02d:%02d", h, m, s)
        } else {
            String.format(Locale.US, "%d:%02d", m, s)
        }
    }

    /** Converts an ISO date string (yyyy-MM-dd) into a friendly display label. */
    fun displayDate(isoDate: String): String = try {
        LocalDate.parse(isoDate).format(displayDateFormatter)
    } catch (_: Exception) {
        isoDate
    }

    /** Today's date as an ISO string, the format persisted in WorkoutSession.date. */
    fun todayIso(): String = LocalDate.now().toString()
}
