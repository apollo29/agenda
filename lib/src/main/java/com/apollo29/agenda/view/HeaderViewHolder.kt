package com.apollo29.agenda.view

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apollo29.agenda.R
import com.apollo29.agenda.model.BaseEvent
import com.apollo29.agenda.util.DateUtils.DATE_FORMAT
import com.apollo29.agenda.util.DateUtils.MONTH_FORMAT
import com.apollo29.agenda.util.DateUtils.isToday
import com.google.android.material.color.MaterialColors

class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    var dateFormat = DATE_FORMAT
    var monthFormat = MONTH_FORMAT

    var dayTextColor: Int = MaterialColors.getColor(view, R.attr.agendaTextColor)
    var currentDayTextColor: Int = MaterialColors.getColor(view, R.attr.agendaTextColor)
    var currentDayBackground: Int = R.drawable.calendar_current_day

    fun bind(event: BaseEvent, showMonth: Boolean) {
        val day = view.findViewById<TextView>(R.id.day)
        val date = view.findViewById<TextView>(R.id.date)
        val month = view.findViewById<TextView>(R.id.month)

        date.text = event.date().dayOfMonth.toString()
        day.text = dateFormat.format(event.date())

        if (event.date().isToday()) {
            date.setBackgroundResource(currentDayBackground)
            date.setTextColor(currentDayTextColor)
        } else {
            date.background = null
            date.setTextColor(dayTextColor)
        }

        if (showMonth) {
            month.visibility = VISIBLE
            month.text = monthFormat.format(event.date())
        } else {
            month.visibility = GONE
        }
    }
}