package com.apollo29.agenda.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollo29.agenda.model.BaseEvent
import com.apollo29.agenda.util.DateProgression
import java.io.IOException
import java.time.LocalDate

abstract class AgendaPagingSource : PagingSource<LocalDate, BaseEvent>() {

    var maxSizeInMonth = 12L
    operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)

    override fun getRefreshKey(state: PagingState<LocalDate, BaseEvent>): LocalDate? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<LocalDate>): LoadResult<LocalDate, BaseEvent> {
        val refresh = params is LoadParams.Refresh
        val loadSize = params.loadSize
        val initialDate = initialDate(params.key, refresh)
        val startDate = startDate(initialDate, loadSize)
        val endDate = endDate(initialDate, loadSize)

        val response = loadData(startDate, endDate)
        val list = fetchData(initialDate, startDate, endDate, response, loadSize)

        val prevKey = prevKey(initialDate, list.first().date())
        val nextKey = nextKey(initialDate, list.last().date())

        return try {
            LoadResult.Page(
                data = list,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        }
    }

    private fun initialDate(initialDate: LocalDate?, refresh: Boolean): LocalDate {
        if (initialDate == null || refresh) {
            return LocalDate.now()
        } else {
            return initialDate
        }
    }

    private fun startDate(initialDate: LocalDate, loadSize: Int): LocalDate {
        if (initialDate.isEqual(LocalDate.now())) {
            return initialStartDate(initialDate, loadSize)
        } else if (initialDate.isBefore(LocalDate.now())) {
            return initialDate.minusDays(loadSize.toLong())
        }
        return initialDate
    }

    private fun endDate(initialDate: LocalDate, loadSize: Int): LocalDate {
        if (initialDate.isEqual(LocalDate.now())) {
            return initialEndDate(initialDate, loadSize)
        } else if (initialDate.isAfter(LocalDate.now())) {
            return initialDate.plusDays(loadSize.toLong())
        }
        return initialDate
    }

    private fun initialStartDate(initialDate: LocalDate, loadSize: Int): LocalDate {
        val minusDays = ((loadSize / 2) - 1).toLong()
        return initialDate.minusDays(minusDays)
    }

    private fun initialEndDate(initialDate: LocalDate, loadSize: Int): LocalDate {
        val plusDays = (loadSize / 2).toLong()
        return initialDate.plusDays(plusDays)
    }

    // next/prevKey

    private fun nextKey(initialDate: LocalDate, endDate: LocalDate): LocalDate? {
        // todo maxdate
        return if (endDate.isEqual(initialDate)) {
            null
        } else {
            endDate.plusDays(1)
        }
    }

    private fun prevKey(initialDate: LocalDate, startDate: LocalDate): LocalDate? {
        // todo maxdate
        return if (startDate.isEqual(initialDate)) {
            null
        } else {
            startDate.minusDays(1)
        }
    }

    // MAX DATE

    private fun maxStartDate(): LocalDate {
        return LocalDate.now().minusMonths(maxSizeInMonth)
    }

    private fun isBeforeMaxDate(date: LocalDate): Boolean {
        val maxDate = maxStartDate()
        return date.isBefore(maxDate)
    }

    private fun maxEndDate(): LocalDate {
        return LocalDate.now().plusMonths(maxSizeInMonth)
    }

    private fun isAfterMaxDate(date: LocalDate): Boolean {
        val maxDate = maxEndDate()
        return date.isAfter(maxDate)
    }

    private fun fetchData(
        initialDate: LocalDate,
        startDate: LocalDate,
        endDate: LocalDate,
        data: Map<LocalDate, List<BaseEvent>>,
        loadSize: Int
    ): List<BaseEvent> {
        val list = mutableListOf<BaseEvent>()
        for (date in startDate..endDate step 1) {
            val events = data[date]
            if (events.isNullOrEmpty()) {
                list.add(BaseEvent.Empty(date))
            } else {
                list.addAll(events)
            }
        }
        return correctToLoadSize(initialDate, list, loadSize)
    }

    private fun correctToLoadSize(
        initialDate: LocalDate,
        list: MutableList<BaseEvent>,
        loadSize: Int
    ): List<BaseEvent> {
        if (list.size > loadSize) {
            val diff = list.size - loadSize
            return if (initialDate.isBefore(LocalDate.now())) {
                list.subList(diff, list.size)
            } else {
                list.subList(0, loadSize)
            }
        }
        return list
    }

    abstract suspend fun loadData(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, List<BaseEvent>>
}