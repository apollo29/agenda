package com.apollo29.agenda.adapter

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apollo29.agenda.R
import com.apollo29.agenda.model.BaseEvent
import com.apollo29.agenda.util.DateUtils.isFirstDayOfWeek
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


class CalendarWeekItemDecoration(private val type: Type = Type.CALENDAR_WEEK) :
    RecyclerView.ItemDecoration() {

    private val firstDayOfWeekFormatter = DateTimeFormatter.ofPattern("d")
    private val lastDayOfWeekFormatter = DateTimeFormatter.ofPattern("d MMM")

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos: Int = parent.getChildAdapterPosition(view)
        if (isFirstDayOfWeek(parent, pos)) {
            outRect.top = 62
        }
    }

    private fun event(parent: RecyclerView, position: Int): BaseEvent? {
        val adapter = parent.adapter
        if (adapter is AgendaAdapter<*, *> && position > -1) {
            return adapter.event(position)
        }
        return null
    }

    private fun isFirstDayOfWeek(parent: RecyclerView, position: Int): Boolean {
        val event = event(parent, position)
        val before = event(parent, position - 1)
        if (event != null && event.date().isFirstDayOfWeek()) {
            if (before != null && event.date() > before.date()) {
                return true
            }
        }
        return false
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter
        if (adapter is AgendaAdapter<*, *>) {
            for (i in 0 until parent.childCount) {
                val child: View = parent.getChildAt(i)
                val position: Int = parent.getChildAdapterPosition(child)

                if (isFirstDayOfWeek(parent, position)) {
                    val view = inflateHeaderView(parent)
                    val event = adapter.event(position)

                    var title = parent.context.getString(
                        R.string.header_calender_week,
                        event.date().format(CALENDAR_WEEK)
                    )
                    if (type == Type.WEEK) {
                        val firstDayOfWeek =
                            event.date().with(WeekFields.of(Locale.getDefault()).firstDayOfWeek)
                        val lastDayOfWeek = firstDayOfWeek.plusDays(6)
                        title = "${firstDayOfWeek.format(firstDayOfWeekFormatter)}–${
                            lastDayOfWeek.format(lastDayOfWeekFormatter)
                        }"
                    }

                    fixLayoutSize(view, parent)
                    view.findViewById<TextView>(R.id.header_calendar_week).text = title
                    drawHeader(c, child, view)
                }
            }
        }
    }

    private fun inflateHeaderView(parent: RecyclerView): View {
        return LayoutInflater.from(parent.context)
            .inflate(R.layout.view_calendarweek_item, parent, false)
    }

    private fun drawHeader(c: Canvas, child: View, headerView: View) {
        c.save()
        c.translate(0F, child.top - headerView.height.toFloat())
        headerView.draw(c)
        c.restore()
    }

    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec: Int = View.MeasureSpec.makeMeasureSpec(
            parent.width,
            View.MeasureSpec.EXACTLY
        )
        val heightSpec: Int = View.MeasureSpec.makeMeasureSpec(
            parent.height,
            View.MeasureSpec.UNSPECIFIED
        )
        val childWidth: Int = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeight: Int = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    enum class Type {
        WEEK,
        CALENDAR_WEEK
    }

    companion object {
        private val CALENDAR_WEEK = DateTimeFormatter.ofPattern("w")
    }
}