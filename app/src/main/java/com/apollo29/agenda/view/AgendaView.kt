package com.apollo29.agenda.view

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollo29.agenda.adapter.AgendaAdapter
import com.apollo29.agenda.adapter.CalendarWeekItemDecoration
import com.apollo29.agenda.adapter.OnEventSetListener
import com.apollo29.agenda.adapter.StickyHeaderDecoration
import com.apollo29.agenda.model.BaseEvent
import com.orhanobut.logger.Logger
import java.time.LocalDate

class AgendaView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    RecyclerView(context, attrs, defStyle) {

    private var onInit = true
    private var eventAdapter: AgendaAdapter<BaseEvent, List<BaseEvent>>? = null
    private val linearLayoutManager: LinearLayoutManager

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = VERTICAL
        super.setLayoutManager(linearLayoutManager)

        itemAnimator = null
        isNestedScrollingEnabled = true
        setHasFixedSize(true)
    }

    var onEventSetListener = object : OnEventSetListener<BaseEvent> {
        override fun onEventSet(events: List<BaseEvent>) {
            Logger.d("onEventSet $onInit ${eventAdapter!!.itemCount}")
            onInit()
        }
    }

    fun onInit() {
        if (onInit && eventAdapter!!.itemCount > 0) {
            Logger.d("onInit true")
            scrollTo(LocalDate.now())
            onInit = false
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter is AgendaAdapter<*, *>) {
            eventAdapter = adapter as AgendaAdapter<BaseEvent, List<BaseEvent>>
            eventAdapter!!.onEventSetListener = onEventSetListener
            super.setAdapter(eventAdapter)
            addItemDecoration(StickyHeaderDecoration(eventAdapter!!.dayHeader, true))
            addItemDecoration(CalendarWeekItemDecoration())

            eventAdapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    eventAdapter!!.refreshEventCache(positionStart, itemCount)
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    eventAdapter!!.refreshEventCache(positionStart, itemCount)
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    eventAdapter!!.refreshEventCache(positionStart, itemCount)
                }

                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    eventAdapter!!.refreshEventCache(fromPosition, itemCount)
                }
            })
            return
        }
        if (isInEditMode) {
            super.setAdapter(adapter)
            return
        }
        throw RuntimeException("Adapter should not be changed")
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun firstVisibleEvent(): BaseEvent? {
        val pos = linearLayoutManager.findFirstVisibleItemPosition()
        return if (pos == NO_POSITION) null else eventAdapter?.event(pos)
    }

    fun scrollTo(localDate: LocalDate?) {
        if (eventAdapter == null) return
        var pos: Int = eventAdapter!!.getAdapterPosition(localDate)
        if (pos >= eventAdapter!!.itemCount) pos = eventAdapter!!.itemCount - 1
        if (pos >= 0) {
            linearLayoutManager.scrollToPositionWithOffset(pos, 0)
        }
    }
}