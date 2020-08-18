package com.r.picturechargingedit.drawers

import android.graphics.Path

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class BlurPath : Path() {

    val pointsX = mutableListOf<Float>()
    val pointsY = mutableListOf<Float>()

    override fun moveTo(x: Float, y: Float) {
        pointsX.add(x)
        pointsY.add(y)
        super.moveTo(x, y)
    }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        pointsX.add(x2)
        pointsY.add(y2)
        super.quadTo(x1, y1, x2, y2)
    }

}
