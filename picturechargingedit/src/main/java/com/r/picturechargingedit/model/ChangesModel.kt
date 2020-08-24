package com.r.picturechargingedit.model

import java.util.*

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

class ChangesModel(val pictureModel: PictureModel, initialRectRadius: Float) {


    private val paths = LinkedList<PathModel>()
    private val rects = LinkedList<RectPathModel>()
    private val colors = LinkedList<RectColorModel>()
    private val lock = Object()

    private var currentPath = PathModel()
    private var currentRectPath = RectPathModel()
    private var currentRectRadius = initialRectRadius


    fun getSize(): Int = paths.size
    fun getColors(): List<RectColorModel> = colors
    fun getRectRadius(): Float = currentRectRadius



    /**
     * sets the colors for each [RectColorModel]
     */
    fun calculateColors() = synchronized(lock) {
        val bitmap = pictureModel.bitmap ?: return@synchronized
        val matrix = pictureModel.matrixInverted()
        for(model in colors) {
            model.calculateColors(bitmap, matrix)
        }
    }

    /**
     * maps all path and rect coordinates back using the inverted [pictureModel]'s matrix
     */
    fun invertAllCoordinates() = synchronized(lock) {
        val matrix = pictureModel.matrixInverted()

        currentRectRadius = matrix.mapRadius(currentRectRadius)

        for(model in paths) {
            for(point in model.getPoints()) {
                matrix.mapPoints(point)
            }
        }

        for(model in rects) {
            for(rect in model.getRects()) {
                matrix.mapRect(rect)
            }
        }

    }

    /**
     * sets pixelated rect radius and re calculated all rects from current paths
     *
     * [rectRadius] the radius which defines
     */
    fun setRectRadius(rectRadius: Float) = synchronized(lock) {
        this.currentRectRadius = rectRadius
        updateRectsFromPaths(rectRadius)
    }


    fun clear() = synchronized(lock) {
        paths.clear()
        rects.clear()
        colors.clear()
    }

    fun removeLast() = synchronized(lock) {
        if(!paths.isEmpty()) paths.removeLast()
        if(!rects.isEmpty()) rects.removeLast()
        if(!colors.isEmpty()) colors.removeLast()
    }


    /**
     * adds a new path starting with the given point
     */
    fun startRecordingDraw(x: Float, y: Float) = synchronized(lock) {
        val newPath = PathModel()
        val newRect = RectPathModel()
        val newColors = RectColorModel(newRect)

        currentPath = newPath
        currentRectPath = newRect

        newPath.add(x, y)
        newRect.add(x, y, currentRectRadius)

        paths.add(newPath)
        rects.add(newRect)
        colors.add(newColors)
    }

    /**
     * continues the current path and adds a point to it
     */
    fun continueRecordingDraw(x: Float, y: Float) = synchronized(lock) {
        if(getSize() == 0) {
            startRecordingDraw(x, y)
        }

        currentPath.add(x, y)
        currentRectPath.add(x, y, currentRectRadius)
    }


    private fun updateRectsFromPaths(rectRadius: Float) {
        rects.clear()
        colors.clear()

        for(path in paths) {
            val rectPath = RectPathModel()
            val rectColor = RectColorModel(rectPath)
            for(point in path.getPoints()) {
                rectPath.add(point[0], point[1], rectRadius)
            }
            rects.add(rectPath)
            colors.add(rectColor)
        }
    }


}
