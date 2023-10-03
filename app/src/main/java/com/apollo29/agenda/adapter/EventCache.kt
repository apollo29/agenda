package com.apollo29.agenda.adapter

import androidx.collection.LongSparseArray
import com.apollo29.agenda.model.BaseEvent
import java.time.LocalDate

object EventCache {
    private val eventCache = LongSparseArray<List<BaseEvent>>()
    fun events(localDate: LocalDate, eventLoader: Loader): List<BaseEvent> {
        val key = getCacheKey(localDate)
        var events = eventCache[key]!!
        if (events.isEmpty()) {
            events = eventLoader.events(localDate)
        }
        eventCache.put(key, events)
        return events
    }

    fun clear(localDate: LocalDate) {
        eventCache.remove(getCacheKey(localDate))
    }

    fun clearAll() {
        eventCache.clear()
    }

    private fun getCacheKey(localDate: LocalDate): Long {
        return localDate.toEpochDay()
    }

    interface Loader {
        fun events(localDate: LocalDate?): List<BaseEvent>
    }
}