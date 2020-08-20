package com.r.picturechargingedit.model

import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 *
 * Author: romanvysotsky
 * Created: 20.08.20
 */

class RectModel {

    private val rects = mutableListOf<RectF>()
    private var previousRect = RectF()

    fun getRects(): List<RectF> {
        return rects
    }

    fun add(x: Float, y: Float, radius: Float) {
        val rect = RectF(x-radius, y-radius, x+radius*2, y+radius*2)

        if(!previousRect.intersects(rect.left, rect.top, rect.right, rect.bottom)) {
            rect.positionNonIntersectingRectsNearby(previousRect)
            rects.add(rect)
            previousRect = rect
        }
    }

    private fun RectF.positionNonIntersectingRectsNearby(previous: RectF) {
        if(previous.isEmpty) {
            return
        }

        if(isAboveOf(previous)) {
            val d = abs(bottom - previous.top)
            bottom = previous.top
            top += d
        }

        if(isBelowOf(previous)) {
            val d = abs(top - previous.bottom)
            top = previous.bottom
            bottom -= d
        }

        if(isRightOf(previous)) {
            val d = abs(left - previous.right)
            left = previous.right
            right -= d
        }

        if(isLeftOf(previous)) {
            val d = abs(right - previous.left)
            right = previous.left
            left += d
        }
    }

    private fun RectF.isLeftOf(rectF: RectF): Boolean {
        return this.right < rectF.left
    }

    private fun RectF.isRightOf(rectF: RectF): Boolean {
        return this.left > rectF.right
    }

    private fun RectF.isAboveOf(rectF: RectF): Boolean {
        return this.bottom < rectF.top
    }

    private fun RectF.isBelowOf(rectF: RectF): Boolean {
        return this.top > rectF.bottom
    }


    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((x2 - x1).pow(2f) - (y2 - y1).pow(2f))
    }

}
