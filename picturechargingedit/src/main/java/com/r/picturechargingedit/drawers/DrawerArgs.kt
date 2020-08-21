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
    val matrix: Matrix = Matrix()
) {

    fun getCurrentBitmap(): Bitmap? {
        return view.getPresenter()?.getBitmap()
    }

    fun getViewWidth(): Int {
        return view.width
    }

    fun getViewHeight(): Int {
        return view.height
    }

    fun createInvertedMatrix(): Matrix {
        val inverted = Matrix()
        matrix.invert(inverted)
        return inverted
    }

}
