package com.apollo29.agenda.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import com.apollo29.agenda.adapter.OnEventSetListener
import com.apollo29.agenda.calendar.CalendarViewListener
import com.apollo29.agenda.databinding.FragmentFirstBinding
import com.apollo29.agenda.model.BaseEvent
import com.apollo29.agenda.util.CalendarUtils.firstDayOfMonth
import com.apollo29.agenda.util.CalendarUtils.yearMonth
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

        // Agenda
        val adapter = TestAdapter()
        binding.agendaView.setAdapter(adapter)
        binding.agendaView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val event = binding.agendaView.firstVisibleEvent()
                if (event != null && newState != SCROLL_STATE_DRAGGING) {
                    Logger.d("SCROLL AGENDA")
                    //binding.calendarView.calendarViewListener?.onMonthScroll(event.date().yearMonth())
                    //binding.calendarView.scrollToDate(event.date())
                    // todo set as selected date?!
                    binding.calendarView.notifyDateChanged(event.date())
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flow.collectLatest {
                adapter.submitData(it)
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
            }
        }

        // Calendar
        binding.calendarView.calendarViewListener = object : CalendarViewListener {
            override fun onDayClick(date: LocalDate?) {
                date?.let {
                    binding.agendaView.scrollTo(it)
                }
                binding.calendarView.toggleCalendar()
            }

            override fun onMonthScroll(yearMonth: YearMonth?) {
                yearMonth?.let {
                    binding.agendaView.scrollTo(it.firstDayOfMonth())
                }

            }

        }

        // END TEST
        scrollToToday()

        binding.fab.setOnClickListener {
            scrollToToday()
        }
    }

    private fun scrollToToday() {
        binding.calendarView.scrollToToday()
        binding.agendaView.scrollToToday()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}