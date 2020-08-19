package com.r.picturechargingedit.model

import java.util.*

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

class ChangesModel {

    private val paths = LinkedList<PathModel>()
    private var currentPath = PathModel()


    fun clear() {
        paths.clear()
    }

    fun getPixelatedPaths(): List<PathModel> {
        return paths
    }

    fun undoLastAction(): Boolean {
        val canRemove = !paths.isEmpty()

        if(canRemove)
            paths.removeLast()

        return canRemove
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

}
