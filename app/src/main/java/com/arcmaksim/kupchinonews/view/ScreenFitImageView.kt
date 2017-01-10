package com.arcmaksim.kupchinonews.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class ScreenFitImageView : ImageView {

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = width * drawable.intrinsicHeight / drawable.intrinsicWidth
        setMeasuredDimension(width, height)
    }

}