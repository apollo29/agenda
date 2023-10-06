package com.apollo29.agenda.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class EventIndicatorView : View {
    private var eventColors: IntArray? = null
    private var radius = 0f
    private var offset = 0f
    private var padding = 0f
    private val dayPaint = Paint()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        radius = (MeasureSpec.getSize(heightMeasureSpec) shr 1).toFloat()
        padding = radius
        offset = (MeasureSpec.getSize(widthMeasureSpec) shr 1).toFloat()
    }

    fun setEventColors(eventColors: IntArray) {
        this.eventColors = eventColors
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawEvents(canvas)
    }

    private fun drawEvents(canvas: Canvas) {
        if (eventColors == null) return
        when (eventColors!!.size) {
            0 -> return
            1 -> drawSingleEvent(canvas, eventColors!![0])
            2 -> drawTwoEvents(canvas, eventColors!!)
            3 -> drawThreeEvents(canvas, eventColors!!)
            else -> drawThreeEventsPlus(canvas, eventColors!!)
        }
    }

    private fun drawSingleEvent(canvas: Canvas, color: Int) {
        drawCircle(canvas, offset, color)
    }

    private fun drawTwoEvents(canvas: Canvas, color: IntArray) {
        drawCircle(canvas, offset - radius - padding / 2, color[0])
        drawCircle(canvas, offset + radius + padding / 2, color[1])
    }

    private fun drawThreeEvents(canvas: Canvas, color: IntArray) {
        drawCircle(canvas, offset - radius * 2 - padding, color[0])
        drawCircle(canvas, offset * 1, color[1])
        drawCircle(canvas, offset + radius * 2 + padding, color[2])
    }

    private fun drawThreeEventsPlus(canvas: Canvas, color: IntArray) {
        drawCircle(canvas, offset - radius * 2 - padding, color[0])
        drawCircle(canvas, offset * 1, color[1])
        drawPlus(canvas, offset + radius * 2 + padding, radius, color[2], (radius / 2).toInt())
    }

    private fun drawCircle(canvas: Canvas, x: Float, color: Int) {
        dayPaint.color = color
        dayPaint.style = Paint.Style.FILL
        canvas.drawCircle(x, radius, radius, dayPaint)
    }

    private fun drawPlus(canvas: Canvas, x: Float, y: Float, color: Int, width: Int) {
        dayPaint.color = color
        dayPaint.strokeWidth = width.toFloat()
        canvas.drawLine(x - radius, y, x + radius, y, dayPaint)
        canvas.drawLine(x, y - radius, x, y + radius, dayPaint)
        dayPaint.strokeWidth = 0f
    }
}