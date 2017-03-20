package com.arcmaksim.kupchinonews.view

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class ScreenFitImageView : AppCompatImageView {

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = drawable?.let {
            width * drawable.intrinsicHeight / drawable.intrinsicWidth
        } ?: 0
        setMeasuredDimension(width, height)
    }

}