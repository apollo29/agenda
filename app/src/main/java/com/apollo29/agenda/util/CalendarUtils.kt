package com.apollo29.agenda.util

import com.apollo29.agenda.util.CalendarUtils.firstDayOfMonth
import com.kizitonwose.calendar.core.CalendarMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

object CalendarUtils {

    fun LocalDate.yearMonth(): YearMonth {
        return YearMonth.from(this)
    }

    fun DayOfWeek.name(textStyle: TextStyle = TextStyle.SHORT_STANDALONE): String {
        return this.getDisplayName(
            textStyle,
            Locale.getDefault()
        )
    }

    fun CalendarMonth.month(textStyle: TextStyle = TextStyle.FULL): String {
        return this.yearMonth.month(textStyle)
    }

    fun CalendarMonth.monthYear(textStyle: TextStyle = TextStyle.FULL): String {
        return this.yearMonth.monthYear(textStyle)
    }

    fun CalendarMonth.firstDayOfMonth(): LocalDate {
        return this.weekDays.first().first().date
    }

    fun YearMonth.firstDayOfMonth(): LocalDate {
        return this.atDay(1)
    }

    fun YearMonth.month(textStyle: TextStyle = TextStyle.FULL): String {
        return this.month.getDisplayName(
            textStyle,
            Locale.getDefault()
        )
    }

    fun YearMonth.monthYear(textStyle: TextStyle = TextStyle.FULL): String {
        val month = this.month(textStyle)
        return "$month ${this.year}"
    }
}