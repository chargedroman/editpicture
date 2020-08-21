package com.r.picturechargingedit.model

import android.graphics.Matrix
import java.util.*

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

class ChangesModel(initialRectRadius: Float) {

    private val paths = LinkedList<PathModel>()
    private val rects = LinkedList<RectPathModel>()
    private val lock = Object()

    private var currentPath = PathModel()
    private var currentRectPath = RectPathModel()
    private var currentRectRadius = initialRectRadius


    fun getSize(): Int = paths.size
    fun getPaths(): List<PathModel> = paths
    fun getRects(): List<RectPathModel> = rects
    fun getRectRadius(): Float = currentRectRadius


    /**
     * maps all path coordinates with the given [matrix] and updates all rects
     */
    fun mapAllCoordinates(matrix: Matrix) = synchronized(lock) {
        for(path in paths) {
            for(point in path.getPoints()) {
                matrix.mapPoints(point)
            }
        }

        currentRectRadius = matrix.mapRadius(currentRectRadius)
        updateRectsFromPaths()
    }

    /**
     * sets pixelated rect radius and re calculated all rects from current paths
     *
     * [rectRadius] the radius which defines
     */
    fun setRectRadius(rectRadius: Float) = synchronized(lock) {
        this.currentRectRadius = rectRadius
        updateRectsFromPaths()
    }


    fun clear() = synchronized(lock) {
        paths.clear()
        rects.clear()
    }

    fun removeLast() = synchronized(lock) {
        if(!paths.isEmpty()) paths.removeLast()
        if(!rects.isEmpty()) rects.removeLast()
    }


    /**
     * adds a new path starting with the given point
     */
    fun startRecordingDraw(x: Float, y: Float) = synchronized(lock) {
        val newPath = PathModel()
        val newRect = RectPathModel()

        currentPath = newPath
        currentRectPath = newRect

        newPath.add(x, y)
        newRect.add(x, y, currentRectRadius)

        paths.add(newPath)
        rects.add(newRect)
    }

    /**
     * continues the current path and adds a point to it
     */
    fun continueRecordingDraw(x: Float, y: Float) = synchronized(lock) {
        currentPath.add(x, y)
        currentRectPath.add(x, y, currentRectRadius)
    }


    private fun updateRectsFromPaths() {
        rects.clear()

        for(path in paths) {
            val rectPath = RectPathModel()
            for(point in path.getPoints()) {
                rectPath.add(point[0], point[1], currentRectRadius)
            }
            rects.add(rectPath)
        }
    }


}
