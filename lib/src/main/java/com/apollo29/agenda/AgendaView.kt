package com.apollo29.agenda

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollo29.agenda.adapter.AgendaAdapter
import com.apollo29.agenda.adapter.CalendarWeekItemDecoration
import com.apollo29.agenda.adapter.StickyHeaderDecoration
import com.apollo29.agenda.model.BaseEvent
import java.time.LocalDate

class AgendaView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    RecyclerView(context, attrs, defStyle) {

    var calendarWeekType = CalendarWeekItemDecoration.Type.WEEK
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
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter is AgendaAdapter<*, *>) {

            eventAdapter = adapter as AgendaAdapter<BaseEvent, List<BaseEvent>>
            super.setAdapter(eventAdapter)
            addItemDecoration(StickyHeaderDecoration(eventAdapter!!.dayHeader, true))
            addItemDecoration(CalendarWeekItemDecoration(calendarWeekType))

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

    fun firstVisibleEvent(): BaseEvent? {
        val pos = linearLayoutManager.findFirstVisibleItemPosition()
        return if (pos == NO_POSITION) null else eventAdapter?.event(pos)
    }

    fun scrollToToday() {
        scrollTo(LocalDate.now())
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