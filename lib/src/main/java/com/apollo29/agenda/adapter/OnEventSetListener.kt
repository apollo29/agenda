package com.apollo29.agenda.adapter

import com.apollo29.agenda.model.BaseEvent

interface OnEventSetListener<T : BaseEvent> {
    fun onEventSet(events: List<T>)
}