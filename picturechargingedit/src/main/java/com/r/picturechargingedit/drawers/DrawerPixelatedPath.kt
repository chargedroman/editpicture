package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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


    fun applyPixelatedChanges(changes: List<PathModel>, canvas: Canvas) {
        val scaleX = 1f
        val scaleY = 1f
        val mappedChanges = changes.map { it.scaleCoordinates(scaleX, scaleY) }
        val paths = mappedChanges.map { it.createPath() }
        for(path in paths) {
            canvas.drawPath(path, pathPaint)
        }
    }

    private fun PathModel.scaleCoordinates(scaleX: Float, scaleY: Float): PathModel {
        val model = PathModel()
        for(point in this.points) {
            model.add(point.scalePoint(scaleX, scaleY))
        }
        return model
    }

    private fun Pair<Float, Float>.scalePoint(scaleX: Float, scaleY: Float): Pair<Float, Float> {
        return Pair(this.first*scaleX, this.second*scaleY)
    }


    fun showPaths(pathModels: List<PathModel>) {
        paths = pathModels.map { it.createPath() }
    }

    fun drawBlurPath(canvas: Canvas) {
        for(path in paths) {
            canvas.drawPath(path, pathPaint)
        }
    }


    private fun PathModel.createPath(): Path {
        if(points.isEmpty()) return Path()

        val path = Path()

        var previousPoint = points.first()
        path.moveTo(previousPoint.first, previousPoint.second)

        for(nextPoint in points) {
            addCurve(path, previousPoint, nextPoint)
            previousPoint = nextPoint
        }

        return path
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

}
