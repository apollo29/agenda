package com.apollo29.agenda.adapter

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * A sticky header decoration for android's RecyclerView.
 */
class StickyHeaderDecoration(adapter: StickyHeaderAdapter, renderInline: Boolean) :
    RecyclerView.ItemDecoration() {

    private val mHeaderCache: MutableMap<Long, RecyclerView.ViewHolder>
    private val mAdapter: StickyHeaderAdapter
    private val mRenderInline: Boolean

    /**
     * @param adapter
     * the sticky header adapter to use
     */
    constructor(adapter: StickyHeaderAdapter) : this(adapter, false)

    /**
     * @param adapter
     * the sticky header adapter to use
     */
    init {
        mAdapter = adapter
        mHeaderCache = HashMap()
        mRenderInline = renderInline
    }

    /**
     * {@inheritDoc}
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position: Int = parent.getChildAdapterPosition(view)
        var headerHeight = 0
        if (position != RecyclerView.NO_POSITION && hasHeader(position)
            && showHeaderAboveItem(position)
        ) {
            val header: View = getHeader(parent, position).itemView
            headerHeight = getHeaderHeightForLayout(header)
        }
        outRect[0, headerHeight, 0] = 0
    }

    private fun showHeaderAboveItem(itemAdapterPosition: Int): Boolean {
        if (itemAdapterPosition == 0) {
            return true
        }
        for (pos in itemAdapterPosition downTo 0) {
            val posHeaderId: Long = mAdapter.getHeaderId(pos)
            if (posHeaderId != mAdapter.getHeaderId(itemAdapterPosition)) {
                if (pos + 1 == itemAdapterPosition) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Clears the header view cache. Headers will be recreated and
     * rebound on list scroll after this method has been called.
     */
    fun clearHeaderCache() {
        mHeaderCache.clear()
    }

    fun findHeaderViewUnder(x: Float, y: Float): View? {
        for (holder in mHeaderCache.values) {
            val child: View = holder.itemView
            val translationX: Float = child.translationX
            val translationY: Float = child.translationY
            if (x >= child.left + translationX && x <= child.right + translationX && y >= child.top + translationY && y <= child.bottom + translationY) {
                return child
            }
        }
        return null
    }

    private fun hasHeader(position: Int): Boolean {
        return mAdapter.getHeaderId(position) != NO_HEADER_ID
    }

    private fun getHeader(parent: RecyclerView, position: Int): RecyclerView.ViewHolder {
        val key: Long = mAdapter.getHeaderId(position)

        return if (mHeaderCache.containsKey(key)) {
            mHeaderCache[key]!!
        } else {
            val holder = mAdapter.onCreateHeaderViewHolder(parent)
            val header = holder.itemView

            mAdapter.onBindHeaderViewHolder(holder, position)
            val widthSpec =
                View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
            val heightSpec =
                View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

            val childWidth = ViewGroup.getChildMeasureSpec(
                widthSpec,
                parent.paddingLeft + parent.paddingRight, header.layoutParams.width
            )
            val childHeight = ViewGroup.getChildMeasureSpec(
                heightSpec,
                parent.paddingTop + parent.paddingBottom, header.layoutParams.height
            )
            header.measure(childWidth, childHeight)
            header.layout(0, 0, header.measuredWidth, header.measuredHeight)
            mHeaderCache[key] = holder
            holder
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val count: Int = parent.childCount
        var previousHeaderId: Long = -1
        for (layoutPos in 0 until count) {
            val child: View = parent.getChildAt(layoutPos)
            val adapterPos: Int = parent.getChildAdapterPosition(child)
            if (adapterPos != RecyclerView.NO_POSITION && hasHeader(adapterPos)) {
                val headerId: Long = mAdapter.getHeaderId(adapterPos)
                if (headerId != previousHeaderId) {
                    previousHeaderId = headerId
                    val header: View = getHeader(parent, adapterPos).itemView
                    canvas.save()
                    val left = child.left
                    val top = getHeaderTop(parent, child, header, adapterPos, layoutPos)
                    canvas.translate(left.toFloat(), top.toFloat())
                    header.translationX = left.toFloat()
                    header.translationY = top.toFloat()
                    header.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    private fun getHeaderTop(
        parent: RecyclerView,
        child: View,
        header: View,
        adapterPos: Int,
        layoutPos: Int
    ): Int {
        val headerHeight = getHeaderHeightForLayout(header)
        var top = child.y.toInt() - headerHeight
        if (layoutPos == 0) {
            val count: Int = parent.childCount
            val currentId: Long = mAdapter.getHeaderId(adapterPos)
            // find next view with header and compute the offscreen push if needed
            for (i in 1 until count) {
                val adapterPosHere: Int = parent.getChildAdapterPosition(parent.getChildAt(i))
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    val nextId: Long = mAdapter.getHeaderId(adapterPosHere)
                    if (nextId != currentId) {
                        val next: View = parent.getChildAt(i)
                        val offset: Int = next.y.toInt() - (headerHeight + getHeader(
                            parent,
                            adapterPosHere
                        ).itemView.height)
                        return if (offset < 0) {
                            offset
                        } else {
                            break
                        }
                    }
                }
            }
            top = 0.coerceAtLeast(top)
        }
        return top
    }

    private fun getHeaderHeightForLayout(header: View): Int {
        return if (mRenderInline) 0 else header.height
    }

    companion object {
        const val NO_HEADER_ID = -1L
    }
}