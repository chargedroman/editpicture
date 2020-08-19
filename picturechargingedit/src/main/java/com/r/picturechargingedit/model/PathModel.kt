package com.r.picturechargingedit.model

import android.graphics.Path
import kotlin.math.abs

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class PathModel {

    val points = mutableListOf<Pair<Float, Float>>()

    fun add(x: Float, y: Float) {
        points.add(Pair(x, y))
    }

}
