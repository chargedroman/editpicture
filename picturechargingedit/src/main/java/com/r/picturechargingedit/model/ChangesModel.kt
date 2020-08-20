package com.r.picturechargingedit.model

import android.graphics.Matrix
import java.util.*

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

class ChangesModel {

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
    fun getRectModels(radius: Float): List<RectModel> {
        return paths.map { it.toRectModel(radius) }
    }


    fun size(): Int {
        return paths.size
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

    fun undoLastAction() {
        val canRemove = !paths.isEmpty()
        if(canRemove) paths.removeLast()
    }


    fun mapAllCoordinates(matrix: Matrix) {
        for(path in paths) {
            for(point in path.getPoints()) {
                matrix.mapPoints(point)
            }
        }
    }


    private fun PathModel.toRectModel(rectRadius: Float): RectModel {
        val model = RectModel()
        for(point in this.getPoints()) {
            model.add(point[0], point[1], rectRadius)
        }
        return model
    }


}
