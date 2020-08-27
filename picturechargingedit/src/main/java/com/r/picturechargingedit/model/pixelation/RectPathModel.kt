package com.r.picturechargingedit.model.pixelation

import android.graphics.RectF
import kotlin.math.abs

/**
 *
 * Author: romanvysotsky
 * Created: 20.08.20
 */

class RectPathModel: RectPath {

    private val rects = mutableListOf<RectF>()
    private var previousRect = RectF()


    override fun getRects(): List<RectF> = rects


    fun add(x: Float, y: Float, radius: Float) {
        val rect = RectF(x-radius, y-radius, x+radius*2, y+radius*2)

        if(!previousRect.intersects(rect.left, rect.top, rect.right, rect.bottom)) {
            rect.positionNonIntersectingRectNear(previousRect)
            rects.splitRectAndAddAll(rect)
            previousRect = rect
        }
    }

    private fun MutableList<RectF>.splitRectAndAddAll(rectF: RectF) {
        val centerX = rectF.centerX()
        val centerY = rectF.centerY()
        val width = abs(rectF.left - centerX)
        val height = abs(rectF.top - centerY)

        val topLeft = RectF(centerX-width, centerY-height, centerX, centerY)
        val topRight = RectF(centerX, centerY-height, centerX+width, centerY)

        val bottomLeft = RectF(centerX-width, centerY, centerX, centerY+height)
        val bottomRight = RectF(centerX, centerY, centerX+width, centerY+height)

        this.add(topLeft)
        this.add(topRight)
        this.add(bottomLeft)
        this.add(bottomRight)
    }


    private fun RectF.positionNonIntersectingRectNear(previous: RectF) {
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


}
