package com.apollo29.agenda.calendar

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
        return this.yearMonth.month.getDisplayName(
            textStyle,
            Locale.getDefault()
        )
    }

    fun CalendarMonth.firstDayOfMonth(): LocalDate {
        return this.weekDays.first().first().date
    }
}