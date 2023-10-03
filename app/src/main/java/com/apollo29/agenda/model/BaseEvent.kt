package com.apollo29.agenda.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface BaseEvent : Comparable<BaseEvent> {

    fun date(): LocalDate

    override fun compareTo(other: BaseEvent): Int {
        val curr: LocalDate = date()

        return if (curr.year == other.date().year && curr.dayOfYear == other.date().dayOfYear) {
            0
        } else {
            curr.compareTo(other.date())
        }
    }

    class Empty(private var date: LocalDate?) : BaseEvent {

        override fun date(): LocalDate {
            return date!!
        }

        fun date(date: LocalDate?) {
            this.date = date
        }

        override fun equals(other: Any?): Boolean {
            return if (other is Empty) {
                compareTo(other) == 0
            } else super.equals(other)
        }

        override fun hashCode(): Int {
            return 0
        }
    }

    companion object {
        val DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }
}