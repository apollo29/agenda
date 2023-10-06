package com.apollo29.agenda.adapter

import android.view.ViewGroup
import com.apollo29.agenda.view.HeaderViewHolder

/**
 * The adapter to assist the [StickyHeaderDecoration] in creating and binding the header views.
 *
 * @param <T> the header view holder
</T> */
interface StickyHeaderAdapter {
    /**
     * Returns the header id for the item at the given position.
     *
     * @param position the item position
     * @return the header id
     */
    fun getHeaderId(position: Int): Long

    /**
     * Creates a new header ViewHolder.
     *
     * @param parent the header's view parent
     * @return a view holder for the created view
     */
    fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderViewHolder

    /**
     * Updates the header view to reflect the header data for the given position
     * @param viewholder the header view holder
     * @param position the header's item position
     */
    fun onBindHeaderViewHolder(viewholder: HeaderViewHolder, position: Int)
}