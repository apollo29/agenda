package com.apollo29.agenda.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import com.apollo29.agenda.R
import com.apollo29.agenda.adapter.AgendaAdapter
import com.apollo29.agenda.adapter.OnEventSetListener
import com.apollo29.agenda.model.BaseEvent
import com.orhanobut.logger.Logger

class TestAdapter(onEventSetListener: OnEventSetListener<BaseEvent>) :
    AgendaAdapter<BaseEvent, List<TestEvent>>(onEventSetListener) {
    override fun createEventViewHolder(viewGroup: ViewGroup): EventViewHolder<BaseEvent> {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.view_empty_item, viewGroup, false)
        return TestEventViewHolder(view)
    }

    override fun onEventClick(event: BaseEvent) {
        Logger.d("ON EVENT CLICK ${event.date()}")
    }
}