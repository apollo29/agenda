package com.apollo29.agenda.view

import android.view.View
import android.widget.TextView
import com.apollo29.agenda.R
import com.apollo29.agenda.adapter.AgendaAdapter
import com.apollo29.agenda.model.BaseEvent
import java.time.LocalDate

class EmptyEventViewHolder(view: View) :
    AgendaAdapter.EventViewHolder<BaseEvent>(view) {

    override fun bind(event: BaseEvent) {
        itemView.findViewById<TextView>(R.id.title).setText(R.string.no_events)
        if (event.date().isBefore(LocalDate.now())) {
            val color = itemView.resources.getColor(R.color.gray, null)
            itemView.findViewById<TextView>(R.id.title).setTextColor(color)
        }
        else {
            val color = itemView.resources.getColor(R.color.black, null)
            itemView.findViewById<TextView>(R.id.title).setTextColor(color)
        }
    }
}