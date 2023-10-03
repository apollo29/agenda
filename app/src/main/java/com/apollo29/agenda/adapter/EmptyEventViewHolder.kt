package com.apollo29.agenda.adapter

import android.view.View
import android.widget.TextView
import com.apollo29.agenda.R
import com.apollo29.agenda.model.BaseEvent
import com.apollo29.agenda.model.BaseEvent.Companion.DATE_FORMAT

class EmptyEventViewHolder(view: View) :
    AgendaAdapter.EventViewHolder<BaseEvent>(view) {

    override fun bind(event: BaseEvent) {
        itemView.findViewById<TextView>(R.id.title).setText(DATE_FORMAT.format(event.date()))
        //itemView.findViewById<TextView>(R.id.title).setText(R.string.no_events)
    }
}