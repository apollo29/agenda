package com.apollo29.agenda.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apollo29.agenda.R
import com.apollo29.agenda.model.BaseEvent
import com.apollo29.agenda.util.DateUtils.isFirstDayOfWeek
import com.apollo29.agenda.view.EmptyEventViewHolder
import com.apollo29.agenda.view.HeaderViewHolder
import com.orhanobut.logger.Logger
import java.time.LocalDate
import java.util.Collections
import kotlin.math.abs

abstract class AgendaAdapter<E : BaseEvent, T : List<E>> :
    PagingDataAdapter<BaseEvent, AgendaAdapter.EventViewHolder<BaseEvent>>(BaseEventComparator()),
    AgendaItemClickListener<BaseEvent> {

    private val eventLoader: EventCache.Loader = object : EventCache.Loader {
        override fun events(localDate: LocalDate?): List<BaseEvent> {
            return eventsOn(localDate)
        }
    }

    lateinit var onEventSetListener: OnEventSetListener<BaseEvent>

    var showMonth = true
    val dayHeader: StickyHeaderAdapter =
        object : StickyHeaderAdapter {
            override fun getHeaderId(position: Int): Long {
                val date = snapshot().items[position].date()
                return date.year * 1000L + date.dayOfYear
            }

            override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_header_item, parent, false)
                return HeaderViewHolder(view)
            }

            override fun onBindHeaderViewHolder(viewholder: HeaderViewHolder, position: Int) {
                val event = snapshot().items[position]
                viewholder.bind(event, showMonth(event, position))
            }

            private fun showMonth(event: BaseEvent, position: Int): Boolean {
                return if (showMonth) {
                    (getHeaderId(position) == getHeaderId(0) || event(position - 1).date().monthValue != event.date().monthValue)
                } else false
            }
        }

    fun refreshEventCache(positionStart: Int, itemCount: Int) {
        val end = positionStart + itemCount
        val events: MutableList<BaseEvent> = ArrayList()
        if (getItemCount() > end) {
            EventCache.clearAll()
        } else {
            var i = positionStart
            while (i < end && i < getItemCount()) {
                val event: BaseEvent = snapshot().items[i]
                EventCache.clear(event.date())
                events.add(event)
                i++
            }
        }
        onEventSetListener.onEventSet(events)
    }

    fun event(position: Int): BaseEvent {
        return getItem(position) ?: key
    }

    fun getAdapterPosition(localDate: LocalDate?): Int {
        val event = BaseEvent.Empty(localDate)
        return run {
            var pos =
                Collections.binarySearch(snapshot().items, event) { obj: BaseEvent, o: BaseEvent? ->
                    obj.compareTo(o!!)
                }
            if (pos >= 0) {
                for (i in pos - 1 downTo 0) {
                    val prevItem: BaseEvent = snapshot().items[i]
                    if (prevItem.compareTo(event) != 0) break
                    pos = i
                }
                pos
            } else {
                abs(pos) - 1
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val event = snapshot().items[position]
        val isFirstDayOfWeek = event.date().isFirstDayOfWeek()
        return if (event is BaseEvent.Empty) {
            if (isFirstDayOfWeek) EMPTY_WEEK_EVENT else EMPTY_EVENT
        } else if (isFirstDayOfWeek) WEEK_EVENT else EVENT
    }

    override fun onBindViewHolder(holder: EventViewHolder<BaseEvent>, position: Int) {
        val event = event(position)
        holder.create(event, this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder<BaseEvent> {
        return if (viewType == EVENT || viewType == WEEK_EVENT) {
            createEventViewHolder(parent)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_empty_item, parent, false)
            EmptyEventViewHolder(view)
        }
    }

    private fun eventsOn(date: LocalDate?): List<BaseEvent> {
        val events = snapshot().items.filter { baseEvent ->
            return@filter baseEvent.date() == date
        }
        return events
    }

    abstract class EventViewHolder<E : BaseEvent>(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun create(event: E, clickListener: AgendaItemClickListener<E>) {
            itemView.setOnClickListener { clickListener.onEventClick(event) }
            bind(event)
        }

        abstract fun bind(event: E)
    }

    abstract fun createEventViewHolder(viewGroup: ViewGroup): EventViewHolder<BaseEvent>

    companion object {
        const val EVENT = 0
        const val EMPTY_EVENT = 1
        const val WEEK_EVENT = 2
        const val EMPTY_WEEK_EVENT = 3
        private val key = BaseEvent.Empty(null)
    }
}