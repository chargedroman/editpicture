package com.r.picturechargingedit.model.crop

import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

class CropModelThumb(pictureModel: Picture) : BaseCropModel(pictureModel) {

    companion object {
        const val THUMBNAIL_ASPECT_RATIO = 3/5f
        const val THUMBNAIL_QUALITY = 90
    }


    private var aspectRatio: Float = THUMBNAIL_ASPECT_RATIO
    private var quality: Int = THUMBNAIL_QUALITY

    fun getAspectRatio(): Float = aspectRatio
    fun getQuality(): Int = quality


    fun setThumbnailParams(aspectRatio: Float, quality: Int) {
        this.aspectRatio = aspectRatio
        this.quality = quality
    }


    override fun limitCroppingRectToOriginalBounds(): Boolean = true

    override fun getDrawMode(): EditPictureMode = EditPictureMode.THUMBNAIL

    override fun getMinWidthCroppingRectLimit(): Float = originalBoundsRect.width()

    override fun getMinHeightCroppingRectLimit(): Float = originalBoundsRect.width()*getAspectRatio()


    override fun calculateCurrentCropArea(event: ScalingMotionEvent): CropArea {
        val inside = touchingInside(event)
        return if(inside)
            CropArea.INSIDE
        else
            CropArea.NONE
    }


}
