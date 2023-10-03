package com.apollo29.agenda.ui

import com.apollo29.agenda.model.BaseEvent
import com.apollo29.agenda.source.AgendaPagingSource
import com.orhanobut.logger.Logger
import java.time.LocalDate

class TestSource : AgendaPagingSource() {
    override fun loadData(startDate: LocalDate, endDate: LocalDate): List<BaseEvent> {
        Logger.d("LOAD DATA TestSource")
        return emptyList()
    }
}