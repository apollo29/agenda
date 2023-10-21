package com.apollo29.agenda

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.apollo29.agenda.calendar.CalendarViewListener
import com.apollo29.agenda.calendar.DayViewContainer
import com.apollo29.agenda.calendar.MonthViewContainer
import com.apollo29.agenda.databinding.CalendarViewBinding
import com.apollo29.agenda.util.CalendarUtils.monthName
import com.apollo29.agenda.util.CalendarUtils.name
import com.apollo29.agenda.util.CalendarUtils.yearMonth
import com.google.android.material.color.MaterialColors
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import java.time.LocalDate
import java.time.YearMonth

class CalendarView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    LinearLayout(context, attrs, defStyle) {

    private var _binding: CalendarViewBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: LocalDate? = null

    var calendarViewListener: CalendarViewListener? = null
    var rangeOfMonthToLoad = 12L

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        _binding = CalendarViewBinding.inflate(LayoutInflater.from(context), this, true)

        // Month
        binding.monthView.text = YearMonth.now().monthName()
        binding.monthView.setOnClickListener {
            toggleCalendar()
        }

        // Calendar
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.textView.text = data.date.dayOfMonth.toString()

                // Show the month dates. Remember that views are reused!
                container.textView.visibility = View.VISIBLE
                if (data.date == selectedDate) {
                    // If this is the selected date, show a round background and change the text color.
                    container.textView.setTextColor(
                        MaterialColors.getColor(
                            binding.root,
                            R.attr.agendaTextColorInverse
                        )
                    )
                    container.textView.setBackgroundResource(R.drawable.calendar_selected_day)
                } else if (data.date == LocalDate.now()) {
                    // If this is the current date, show a circle.
                    container.textView.setTextColor(
                        MaterialColors.getColor(
                            binding.root,
                            R.attr.agendaTextColor
                        )
                    )
                    container.textView.setBackgroundResource(R.drawable.calendar_current_day)
                } else {
                    // If this is NOT the selected date, remove the background and reset the text color.
                    container.textView.setTextColor(
                        MaterialColors.getColor(
                            binding.root,
                            R.attr.agendaTextColor
                        )
                    )
                    container.textView.background = null
                }

                if (data.position != DayPosition.MonthDate) {
                    container.textView.setTextColor(
                        MaterialColors.getColor(
                            binding.root,
                            R.attr.agendaTextColorDisabled
                        )
                    )
                }

                container.view.setOnClickListener {
                    // Keep a reference to any previous selection
                    // in case we overwrite it and need to reload it.
                    val currentSelection = selectedDate
                    if (currentSelection == data.date) {
                        // If the user clicks the same date, clear selection.
                        selectedDate = null
                        // Reload this date so the dayBinder is called
                        // and we can REMOVE the selection background.
                        binding.calendarView.notifyDateChanged(currentSelection)
                    } else {
                        selectedDate = data.date
                        // Reload the newly selected date so the dayBinder is
                        // called and we can ADD the selection background.
                        binding.calendarView.notifyDateChanged(data.date)
                        if (currentSelection != null) {
                            // We need to also reload the previously selected
                            // date so we can REMOVE the selection background.
                            binding.calendarView.notifyDateChanged(currentSelection)
                        }
                    }

                    if (data.position != DayPosition.MonthDate) {
                        binding.calendarView.scrollToDate(data.date, DayPosition.MonthDate)
                    }

                    calendarViewListener?.onDayClick(selectedDate)
                }
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(rangeOfMonthToLoad)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(rangeOfMonthToLoad)  // Adjust as needed
        val daysOfWeek = daysOfWeek()

        binding.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendarView.scrollToMonth(currentMonth)
        binding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    if (container.titlesContainer.tag == null) {
                        container.titlesContainer.tag = data.yearMonth
                        container.titlesContainer.children.map { it as TextView }
                            .forEachIndexed { index, textView ->
                                val dayOfWeek = daysOfWeek[index]
                                val title = dayOfWeek.name()
                                textView.text = title
                            }
                    }
                }
            }

        binding.calendarView.monthScrollListener = object : MonthScrollListener {
            override fun invoke(calendarMonth: CalendarMonth) {
                monthView(calendarMonth.yearMonth)
                calendarViewListener?.onMonthScroll(calendarMonth.yearMonth)
            }
        }
    }

    private fun monthView(yearMonth: YearMonth) {
        binding.monthView.text = yearMonth.monthName()
    }

    fun scrollToToday() {
        scrollToDate(LocalDate.now())
    }

    fun scrollToDate(date: LocalDate, position: DayPosition = DayPosition.MonthDate) {
        selectedDate?.let {
            selectedDate = null
            binding.calendarView.notifyDateChanged(it)
        }
        binding.calendarView.scrollToDate(date, position)
        calendarViewListener?.onMonthScroll(date.yearMonth())
    }

    fun notifyDateChanged(localDate: LocalDate) {
        notifyMonthChanged(localDate.yearMonth())
    }

    fun notifyMonthChanged(yearMonth: YearMonth) {
        monthView(yearMonth)
        binding.calendarView.notifyMonthChanged(yearMonth)
        binding.calendarView.scrollToMonth(yearMonth)
    }

    fun toggleCalendar() {
        binding.monthView.isChecked = !binding.monthView.isChecked
        if (binding.monthView.isChecked) {
            binding.calendarView.visibility = VISIBLE
        } else {
            binding.calendarView.visibility = GONE
        }
    }
}