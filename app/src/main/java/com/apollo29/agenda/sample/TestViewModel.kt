package com.apollo29.agenda.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class TestViewModel : ViewModel() {

    val flow = Pager(
        PagingConfig(
            pageSize = 30,
            initialLoadSize = 90,
            enablePlaceholders = false
        )
    ) {
        TestSource()
    }.flow.cachedIn(viewModelScope)
}