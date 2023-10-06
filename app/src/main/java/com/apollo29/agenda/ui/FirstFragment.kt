package com.apollo29.agenda.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.apollo29.agenda.R
import com.apollo29.agenda.adapter.OnEventSetListener
import com.apollo29.agenda.util.CalendarUtils.name
import com.apollo29.agenda.util.CalendarUtils.yearMonth
import com.apollo29.agenda.calendar.DayViewContainer
import com.apollo29.agenda.calendar.MonthViewContainer
import com.apollo29.agenda.databinding.FragmentFirstBinding
import com.apollo29.agenda.model.BaseEvent
import com.apollo29.agenda.util.CalendarUtils.monthYear
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.MonthScrollListener
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TestViewModel

    private var selectedDate: LocalDate? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[TestViewModel::class.java]
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TEST

        // Month
        // todo sync today with cal and agenda
        binding.monthView.text = YearMonth.now().monthYear()
        binding.monthView.setOnClickListener {
            binding.monthView.isChecked = !binding.monthView.isChecked
            if (binding.monthView.isChecked) {
                binding.calendarView.visibility = VISIBLE
            }
            else {
                binding.calendarView.visibility = GONE
            }
        }

        // Agenda
        val adapter = TestAdapter()
        binding.agendaView.setAdapter(adapter)
        binding.agendaView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val event = binding.agendaView.firstVisibleEvent()
                if (event != null && newState != SCROLL_STATE_DRAGGING) {
                    Logger.d("SCROLL AGENDA")
                    binding.calendarView.scrollToDate(event.date(), DayPosition.MonthDate)
                    // todo set as selected date?!
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flow.collectLatest {
                adapter.submitData(it)
            }
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
                    container.textView.setTextColor(Color.WHITE)
                    container.textView.setBackgroundResource(R.drawable.calendar_selected_day)
                } else if (data.date == LocalDate.now()) {
                    // If this is the current date, show a circle.
                    container.textView.setTextColor(Color.BLACK)
                    container.textView.setBackgroundResource(R.drawable.calendar_current_day)
                } else {
                    // If this is NOT the selected date, remove the background and reset the text color.
                    container.textView.setTextColor(Color.BLACK)
                    container.textView.background = null
                }

                if (data.position != DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.GRAY)
                }

                container.view.setOnClickListener {
                    binding.agendaView.scrollTo(data.date)

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
                }
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
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
                Logger.d("SCROLL MonthScrollListener")
                binding.monthView.text = calendarMonth.monthYear()
            }
        }

        binding.agendaView.onEventSetListener = object : OnEventSetListener<BaseEvent> {
            override fun onEventSet(events: List<BaseEvent>) {
                if (events.isNotEmpty()) {
                    val yearMonths: MutableSet<YearMonth> = HashSet()
                    events.forEach {
                        val yearMonth = it.date().yearMonth()
                        if (yearMonths.add(yearMonth)) {
                            binding.calendarView.notifyMonthChanged(yearMonth)
                        }
                    }
                }

                binding.agendaView.onInit()
            }
        }

        // END TEST

        binding.fab.setOnClickListener {
            selectedDate?.let {
                selectedDate = null
                binding.calendarView.notifyDateChanged(it)
            }
            binding.calendarView.scrollToDate(LocalDate.now(), DayPosition.MonthDate)
            binding.agendaView.scrollTo(LocalDate.now())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}