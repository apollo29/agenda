package com.apollo29.agenda.calendar

import android.view.View
import android.widget.TextView
import com.apollo29.agenda.R
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.calendarDayText)
}