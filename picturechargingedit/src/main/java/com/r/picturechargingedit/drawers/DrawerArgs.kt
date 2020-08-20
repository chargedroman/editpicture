package com.r.picturechargingedit.drawers

import android.graphics.Bitmap
import android.graphics.Matrix
import com.r.picturechargingedit.mvp.EditPictureView

/**
 *
 * Author: romanvysotsky
 * Created: 20.08.20
 */

class DrawerArgs(
    private val view: EditPictureView,
    val matrix: Matrix = Matrix(),
    val radius: Float = RECT_RADIUS,
    var bitmap: Bitmap? = null
) {

    companion object {
        const val RECT_RADIUS = 20f
    }


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
