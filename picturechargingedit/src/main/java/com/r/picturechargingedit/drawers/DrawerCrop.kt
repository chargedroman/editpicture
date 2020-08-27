package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.r.picturechargingedit.arch.Drawer
import com.r.picturechargingedit.model.crop.Crop

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerCrop: Drawer<Crop>() {

    private val pathPaint = Paint()

    init {
        pathPaint.color = Color.GREEN
        pathPaint.style = Paint.Style.STROKE
        pathPaint.strokeCap = Paint.Cap.SQUARE
    }


    override fun drawChangesOnCanvas(changes: Crop, canvas: Canvas) {
        pathPaint.strokeWidth = changes.getCroppingRectRadius()
        canvas.drawRect(changes.getCroppingRect(), pathPaint)
    }

}
