package com.r.picturechargingedit.model

/**
 *
 * Author: romanvysotsky
 * Created: 25.08.20
 */

interface Changes {

    fun getColors(): List<RectColorModel>
    fun getPictureModel(): Picture
    fun getRectRadius(): Float
    fun setRectRadius(rectRadius: Float)

    fun calculateColors()
    fun invertAllCoordinates()

    fun clear()
    fun getSize(): Int
    fun removeLast()

    fun startRecordingDraw(x: Float, y: Float)
    fun continueRecordingDraw(x: Float, y: Float)

}
