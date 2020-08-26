package com.r.picturechargingedit.scale


/**
 *
 * Author: romanvysotsky
 * Created: 26.08.20
 */

interface Scaling {

    fun setTranslateScaleEnabled(translate: Boolean, scale: Boolean)
    fun setMinMaxScale(minScale: Float, maxScale: Float)
    fun setBoundsWidthHeight(width: Int, height: Int)
    fun getCurrentScalingFactor(): Float

}
