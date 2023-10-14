package com.apollo29.agenda.model

import java.time.LocalDate

interface BaseEvent : Comparable<BaseEvent> {

    fun date(): LocalDate

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int

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
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Empty

            if (date != other.date) return false

            return true
        }

        override fun hashCode(): Int {
            return 0
        }
    }
}