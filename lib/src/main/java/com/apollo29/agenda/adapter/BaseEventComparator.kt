package com.apollo29.agenda.adapter

import androidx.recyclerview.widget.DiffUtil
import com.apollo29.agenda.model.BaseEvent

class BaseEventComparator : DiffUtil.ItemCallback<BaseEvent>() {
    override fun areItemsTheSame(oldItem: BaseEvent, newItem: BaseEvent) =
        oldItem.equals(newItem)

    override fun areContentsTheSame(oldItem: BaseEvent, newItem: BaseEvent) =
        oldItem.equals(newItem)
}