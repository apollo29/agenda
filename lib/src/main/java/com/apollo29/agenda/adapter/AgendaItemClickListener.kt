package com.apollo29.agenda.adapter

import com.apollo29.agenda.model.BaseEvent

interface AgendaItemClickListener<E : BaseEvent> {

    fun onEventClick(event: E)
}