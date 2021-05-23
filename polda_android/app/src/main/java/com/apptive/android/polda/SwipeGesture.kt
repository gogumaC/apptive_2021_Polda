package com.apptive.android.polda

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

abstract class SwipeGesture(v: View) :GestureDetector.OnGestureListener{
    private val SWIPE_THRESHOLD=100
    private val SWIPE_VELOCITY_THRESHOLD=100
    val v=v
    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        var result = false
        try {
            val diffY = p1!!.y - p0!!.y
            val diffX = p1.x - p0.x
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(p2) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()

                    } else {
                        onSwipeLeft()
                    }
                }
                result = true
            }
            result = true
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return result
    }

    fun onSwipeRight(){}
    fun onSwipeLeft(){}

    override fun onDown(p0: MotionEvent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onShowPress(p0: MotionEvent?) {
        TODO("Not yet implemented")
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        TODO("Not yet implemented")
    }

    override fun onLongPress(p0: MotionEvent?) {
        TODO("Not yet implemented")
    }
}