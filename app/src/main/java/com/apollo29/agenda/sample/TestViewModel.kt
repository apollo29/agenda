package com.apollo29.agenda.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TestViewModel : ViewModel() {

    val flow = Pager(
        PagingConfig(
            pageSize = 60
        )
    ) {
        TestSource()
    }.flow.map {
        val data = mutableSetOf<LocalDate>()
        it.filter { event ->
            if (data.contains(event.date())) {
                false
            } else {
                data.add(event.date())
            }
        }
    }.cachedIn(viewModelScope)

}