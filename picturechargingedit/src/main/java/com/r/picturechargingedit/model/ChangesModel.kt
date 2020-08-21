package com.r.picturechargingedit.model

import android.graphics.Matrix
import java.util.*

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

class ChangesModel(val initialRectRadius: Float) {

    private val paths = LinkedList<PathModel>()
    private var currentPath = PathModel()


    /**
     * get raw paths like from point a to b
     */
    fun getPaths(): List<PathModel> {
        return paths
    }

    /**
     * get ready to draw rects from the current path
     */
    fun getRectsAlongPath(rectRadius: Float): List<RectPathModel> {
        return paths.map { it.toRectModel(rectRadius) }
    }


    fun size(): Int {
        return paths.size
    }

    fun clear() {
        paths.clear()
    }

    fun removeLast() {
        if(!paths.isEmpty()) paths.removeLast()
    }


    fun startRecordingDraw(x: Float, y: Float) {
        val newPath = PathModel()
        currentPath = newPath
        newPath.add(x, y)
        paths.add(newPath)
    }

    fun continueRecordingDraw(x: Float, y: Float) {
        currentPath.add(x, y)
    }


    fun mapAllCoordinates(matrix: Matrix) {
        for(path in paths) {
            for(point in path.getPoints()) {
                matrix.mapPoints(point)
            }
        }
    }


    private fun PathModel.toRectModel(rectRadius: Float): RectPathModel {
        val model = RectPathModel()
        for(point in this.getPoints()) {
            model.add(point[0], point[1], rectRadius)
        }
        return model
    }


}
