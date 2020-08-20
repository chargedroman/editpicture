package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.r.picturechargingedit.model.ChangesModel
import com.r.picturechargingedit.model.PathModel
import kotlin.math.abs

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerPixelatedPath(private val drawerArgs: DrawerArgs) {

    companion object {
        private const val TOUCH_TOLERANCE = 4
    }

    private var paths = listOf<Path>()

    private val pathPaint = Paint()


    init {
        pathPaint.color = Color.WHITE
        pathPaint.style = Paint.Style.STROKE
        pathPaint.strokeJoin = Paint.Join.ROUND
        pathPaint.strokeCap = Paint.Cap.ROUND
        pathPaint.strokeWidth = 20f
    }


    fun applyChanges(changes: ChangesModel, canvas: Canvas) {
        val paths = changes.getPixelatedPaths().map { it.createPath() }
        for(path in paths) {
            canvas.drawPath(path, pathPaint)
        }
    }


    fun showPaths(changes: ChangesModel) {
        paths = changes.getPixelatedPaths().map { it.createPath() }
    }

    fun drawBlurPath(canvas: Canvas) {
        for(path in paths) {
            canvas.drawPath(path, pathPaint)
        }
    }


    private fun PathModel.createPath(): Path {
        if(getPoints().isEmpty()) return Path()

        val path = Path()

        var previousPoint = getPoints().first()
        path.moveTo(previousPoint[0], previousPoint[1])

        for(nextPoint in getPoints()) {
            addCurve(path, previousPoint, nextPoint)
            previousPoint = nextPoint
        }

        return path
    }

    private fun addCurve(path: Path, previous: FloatArray, next: FloatArray) {
        val dx: Float = abs(next[0] - previous[0])
        val dy: Float = abs(next[1] - previous[1])

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(
                previous[0],
                previous[1],
                (next[0] + previous[0])/2,
                (next[1] + previous[1])/2
            )
        }
    }

}
