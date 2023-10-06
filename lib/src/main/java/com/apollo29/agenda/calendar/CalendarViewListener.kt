package com.apollo29.agenda.calendar

import java.time.LocalDate
import java.time.YearMonth

interface CalendarViewListener {

    fun onDayClick(date: LocalDate?)

    fun onMonthScroll(yearMonth: YearMonth?)
}