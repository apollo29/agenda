package com.apollo29.agenda.sample

import com.apollo29.agenda.model.BaseEvent
import com.apollo29.agenda.source.AgendaPagingSource
import java.time.LocalDate
import kotlin.random.Random

class TestSource : AgendaPagingSource() {
    override suspend fun loadData(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, List<BaseEvent>> {
        val list = mutableMapOf<LocalDate, List<BaseEvent>>()
        for (date in startDate..endDate step 3) {
            list[date] = testEvents(date)
        }
        return list
    }

    private fun testEvents(date: LocalDate): List<TestEvent> {
        val list = mutableListOf<TestEvent>()
        val amount = Random.nextInt(1, 3)
        for (i in amount downTo 0 step 1) {
            list.add(TestEvent(date))
        }
        return list
    }
}