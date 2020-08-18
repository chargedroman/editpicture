package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.r.picturechargingedit.EditPictureView
import com.r.picturechargingedit.model.PathModel

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerPathModel(private val view: EditPictureView) {

    private var paths = listOf<Path>()

    private val pathPaint = Paint()


    init {
        pathPaint.color = Color.WHITE
        pathPaint.style = Paint.Style.STROKE
        pathPaint.strokeJoin = Paint.Join.ROUND
        pathPaint.strokeCap = Paint.Cap.ROUND
        pathPaint.strokeWidth = 20f
    }


    fun onNextPathModels(pathModels: List<PathModel>) {
        paths = pathModels.map { val path = Path(); it.fillPath(path); path }
        view.invalidate()
    }

    fun drawBlurPath(canvas: Canvas) {
        for(path in paths) {
            canvas.drawPath(path, pathPaint)
        }
    }

}
