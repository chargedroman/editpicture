package com.r.picturechargingedit.model.changes

import com.r.picturechargingedit.model.picture.Picture

/**
 *
 * Author: romanvysotsky
 * Created: 25.08.20
 */

interface Changes {

    fun getColorModels(): List<RectColor>
    fun getPictureModel(): Picture
    fun getRectRadiusFactor(): Float
    fun setRectRadiusFactor(rectRadiusFactor: Float)

    fun calculateColors()
    fun invertAllCoordinates()

    fun clear()
    fun getSize(): Int
    fun removeLast()

    fun startRecordingDraw(x: Float, y: Float, radius: Float)
    fun continueRecordingDraw(x: Float, y: Float, radius: Float)

}
