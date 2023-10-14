package com.apollo29.agenda.sample

import com.apollo29.agenda.model.BaseEvent
import java.time.LocalDate

class TestEvent(private val date: LocalDate) : BaseEvent {
    override fun date(): LocalDate {
        return date
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestEvent

        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        return date.hashCode()
    }


}