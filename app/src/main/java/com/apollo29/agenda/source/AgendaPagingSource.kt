package com.apollo29.agenda.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollo29.agenda.model.BaseEvent
import com.apollo29.agenda.util.DateProgression
import com.orhanobut.logger.Logger
import java.io.IOException
import java.time.LocalDate

abstract class AgendaPagingSource : PagingSource<LocalDate, BaseEvent>() {

    operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)

    override fun getRefreshKey(state: PagingState<LocalDate, BaseEvent>): LocalDate? {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<LocalDate>): LoadResult<LocalDate, BaseEvent> {
        Logger.d("INIT ${params.key}")

        val initialDate = params.key ?: LocalDate.now()
        val startDate = initialDate.minusDays(30)
        val endDate = initialDate.plusDays(30)

        val response = loadData(startDate, endDate)
        val list = fetchData(startDate, endDate, response)

        Logger.d("Page ${params.key} - ${params.loadSize} / InitialDate $initialDate / StartDate $startDate / EndDate $endDate")

        return try {
            LoadResult.Page(
                data = list,
                prevKey = startDate,
                nextKey = endDate
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        }
    }

    private fun fetchData(
        startDate: LocalDate,
        endDate: LocalDate,
        data: List<BaseEvent>
    ): List<BaseEvent> {
        Logger.d("FETCH DATA")
        Logger.d(data)

        val list = mutableListOf<BaseEvent>()
        for (date in startDate..endDate step 1) {
            list.add(BaseEvent.Empty(date))
        }
        return list
    }

    abstract fun loadData(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<BaseEvent>
}