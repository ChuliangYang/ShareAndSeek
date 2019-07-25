package com.me.cl.capstoneproject.widget

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.View

/**
 * Created by CL on 1/15/18.
 */
class CustomNestedScrollView:NestedScrollView {
    var includeSwipeRefreshLayout:Boolean=false
    var overScroll:Boolean=false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (includeSwipeRefreshLayout) {
            if (dy > 0) {
                scrollBy(0, dy)
                consumed[1] = dy
            }
        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        if (includeSwipeRefreshLayout) {
            fling(velocityY.toInt())
            return false
        } else {
            return super.onNestedPreFling(target, velocityX, velocityY)
        }
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        if (includeSwipeRefreshLayout) {
            return false
        } else {
            return super.onNestedFling(target, velocityX, velocityY, consumed)
        }
    }

}