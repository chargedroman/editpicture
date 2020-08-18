package com.r.picturechargingedit.model

import android.graphics.Path
import kotlin.math.abs

/**
 * Responsible for saving points and transforming them
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class PathModel(private val width: Int, private val height: Int) {

    companion object {
        private const val TOUCH_TOLERANCE = 4
    }


    private val points = mutableListOf<Pair<Float, Float>>()



    fun add(x: Float, y: Float) {
        points.add(Pair(x, y))
    }


    fun fillPath(path: Path) {
        path.reset()
        if(points.isEmpty()) return

        var previousPoint = points.first()
        path.moveTo(previousPoint.first, previousPoint.second)

        for(nextPoint in points) {
            addCurve(path, previousPoint, nextPoint)
            previousPoint = nextPoint
        }
    }

    private fun addCurve(path: Path, previous: Pair<Float, Float>, next: Pair<Float, Float>) {
        val dx: Float = abs(next.first - previous.first)
        val dy: Float = abs(next.second - previous.second)

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(
                previous.first,
                previous.second,
                (next.first + previous.first)/2,
                (next.second + previous.second)/2
            )
        }
    }


    fun fillPath(path: Path, width: Int, height: Int) {

    }


}
