package com.r.picturechargingedit.model.pixelation

import android.graphics.Rect

/**
 *
 * Author: romanvysotsky
 * Created: 25.08.20
 */

interface Pixelation {

    fun getColorModels(): List<RectColor>

    fun mapCoordinates()
    fun mapCoordinatesInverted()
    fun mapCoordinatesTo(croppedRect: Rect)

    fun clear()
    fun getSize(): Int
    fun removeLast()

    fun startRecordingDraw(x: Float, y: Float, radius: Float)
    fun continueRecordingDraw(x: Float, y: Float, radius: Float)

}
