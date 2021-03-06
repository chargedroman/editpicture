package com.r.picturechargingedit.model.pixelation

import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import com.r.picturechargingedit.model.picture.Picture
import java.util.*

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

class PixelationModel(private val pictureModel: Picture): Pixelation {


    private val paths = LinkedList<PathModel>()
    private val rects = LinkedList<RectPathModel>()
    private val colors = LinkedList<RectColorModel>()
    private val lock = Object()
    private val bufferMatrix = Matrix()

    private var currentPath = PathModel()
    private var currentRectPath = RectPathModel()


    override fun getSize(): Int = paths.size
    override fun getColorModels(): List<RectColorModel> = colors


    /**
     * maps all path and rect coordinates back using the inverted [pictureModel]'s matrix
     */
    override fun mapCoordinatesInverted() = synchronized(lock) {
        mapCoordinates(pictureModel.getMatrixInverted())
    }

    /**
     * maps all path and rect coordinates back using the [pictureModel]'s matrix
     */
    override fun mapCoordinates() = synchronized(lock) {
        mapCoordinates(pictureModel.getMatrix())
    }

    /**
     * maps all coordinates from the original bitmap rect to the cropped bitmap rect
     */
    override fun mapCoordinatesTo(croppedRect: Rect) = synchronized(lock) {

        val cropRect = RectF(croppedRect)
        val oldBitmapRect = pictureModel.getBitmapBounds()

        mapCoordinatesInverted()
        bufferMatrix.setRectToRect(cropRect, oldBitmapRect, Matrix.ScaleToFit.CENTER)
        mapCoordinates(bufferMatrix)
        mapCoordinates()

    }
    

    override fun clear() = synchronized(lock) {
        paths.clear()
        rects.clear()
        colors.clear()
    }

    override fun removeLast() = synchronized(lock) {
        if(!paths.isEmpty()) paths.removeLast()
        if(!rects.isEmpty()) rects.removeLast()
        if(!colors.isEmpty()) colors.removeLast()
    }


    /**
     * adds a new path starting with the given point
     */
    override fun startRecordingDraw(x: Float, y: Float, radius: Float) = synchronized(lock) {
        val newPath = PathModel()
        val newRect = RectPathModel()
        val newColors = RectColorModel(newRect)

        currentPath = newPath
        currentRectPath = newRect

        newPath.add(x, y)
        newRect.add(x, y, radius)

        paths.add(newPath)
        rects.add(newRect)
        colors.add(newColors)

        calculateColors()
    }

    /**
     * continues the current path and adds a point to it
     */
    override fun continueRecordingDraw(x: Float, y: Float, radius: Float) = synchronized(lock) {
        currentPath.add(x, y)
        currentRectPath.add(x, y, radius)
        calculateColors()
    }


    /**
     * sets the colors for each [RectColorModel]
     */
    private fun calculateColors() = synchronized(lock) {
        val bitmap = pictureModel.getBitmap() ?: return@synchronized
        val matrix = pictureModel.getMatrixInverted()
        for(model in colors) {
            model.calculateColors(bitmap, matrix)
        }
    }


    /**
     * uses [matrix] to map all coordinates in this
     */
    private fun mapCoordinates(matrix: Matrix) {
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

}
