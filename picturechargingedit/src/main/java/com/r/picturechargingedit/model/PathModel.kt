package com.r.picturechargingedit.model

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class PathModel {

    private val points = mutableListOf<FloatArray>()

    fun getPoints(): List<FloatArray> {
        return points
    }

    fun add(x: Float, y: Float) {
        points.add(floatArrayOf(x, y))
    }

}
