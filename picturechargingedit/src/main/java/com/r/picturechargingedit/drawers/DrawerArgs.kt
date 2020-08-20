package com.r.picturechargingedit.drawers

import android.graphics.Bitmap
import android.graphics.Matrix
import com.r.picturechargingedit.v2.EditPictureView

/**
 *
 * Author: romanvysotsky
 * Created: 20.08.20
 */

class DrawerArgs(
    private val view: EditPictureView,
    val matrix: Matrix = Matrix(),
    var radius: Float = 0f,
    var bitmap: Bitmap? = null
) {

    fun getOriginalViewWidth(): Int {
        return view.width
    }

    fun getOriginalViewHeight(): Int {
        return view.height
    }

    fun createInvertedMatrix(): Matrix {
        val inverted = Matrix()
        matrix.invert(inverted)
        return inverted
    }

}
