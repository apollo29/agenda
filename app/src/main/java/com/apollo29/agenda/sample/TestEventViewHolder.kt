package com.apollo29.agenda.sample

import android.view.View
import android.widget.TextView
import com.apollo29.agenda.R
import com.apollo29.agenda.adapter.AgendaAdapter
import com.apollo29.agenda.model.BaseEvent
import java.time.format.DateTimeFormatter

class TestEventViewHolder(view: View) :
    AgendaAdapter.EventViewHolder<BaseEvent>(view) {

    val STANDARD_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    override fun bind(event: BaseEvent) {
        itemView.findViewById<TextView>(R.id.title).text = "Test Event \n"+event.date().format(STANDARD_DATE)
    }
}