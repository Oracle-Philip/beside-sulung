package com.rummy.sulung.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.view.ViewConfigurationCompat

import androidx.core.widget.NestedScrollView

class CustomSulungNestedScrollView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private var initialX = 0f
    private var initialY = 0f

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x
                initialY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - initialX
                val deltaY = event.y - initialY

                if (Math.abs(deltaX) > touchSlop && Math.abs(deltaX) > Math.abs(deltaY)) {
                    return false // Don't intercept the event, pass it to the ViewPager
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }
}