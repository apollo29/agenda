package com.apollo29.agenda.util

import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale

object DateUtils {

    fun Calendar.isSameDay(newDate: Calendar): Boolean =
        this.get(Calendar.DAY_OF_MONTH) == newDate.get(Calendar.DAY_OF_MONTH)

    fun LocalDate.isSameDay(): Boolean = LocalDate.now() == this

    fun LocalDate.isToday(): Boolean {
        val today = LocalDate.now()
        return (today.year == this.year && today.dayOfYear == this.dayOfYear)
    }

    fun LocalDate.isFirstDayOfWeek(): Boolean {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        return this.dayOfWeek == firstDayOfWeek
    }

    val DATE_FORMAT = DateTimeFormatterBuilder()
        .appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT_STANDALONE)
        .toFormatter()

    val MONTH_FORMAT = DateTimeFormatterBuilder()
        .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
        .toFormatter()
}