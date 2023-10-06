package com.apollo29.agenda.view

import android.view.View
import android.widget.TextView
import com.apollo29.agenda.R
import com.apollo29.agenda.adapter.AgendaAdapter
import com.apollo29.agenda.model.BaseEvent

class EmptyEventViewHolder(view: View) :
    AgendaAdapter.EventViewHolder<BaseEvent>(view) {

    override fun bind(event: BaseEvent) {
        itemView.findViewById<TextView>(R.id.title).setText(R.string.no_events)
    }
}