package com.r.picturechargingedit.model.crop

import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

class CropModelCrop(pictureModel: Picture) : BaseCropModel(pictureModel) {


    override fun limitCroppingRectToOriginalBounds(): Boolean = false

    override fun getDrawMode(): EditPictureMode = EditPictureMode.CROP

    override fun getMinWidthCroppingRectLimit(): Float = getCroppingRectRadius()*MINSIZE_FACTOR

    override fun getMinHeightCroppingRectLimit(): Float = getCroppingRectRadius()*MINSIZE_FACTOR


    override fun calculateCurrentCropArea(event: ScalingMotionEvent): CropArea {

        val inside = touchingInside(event)
        val left = touchingLeftHitBox(event)
        val right = touchingRightHitBox(event)
        val top = touchingTopHitBox(event)
        val bottom = touchingBottomHitBox(event)

        return if(top && left) {
            CropArea.TOP_LEFT
        } else if(top && right) {
            CropArea.TOP_RIGHT
        } else if(bottom && left) {
            CropArea.BOTTOM_LEFT
        } else if(bottom && right) {
            CropArea.BOTTOM_RIGHT
        } else if(top) {
            CropArea.TOP
        } else if(left) {
            CropArea.LEFT
        } else if(bottom) {
            CropArea.BOTTOM
        } else if(right) {
            CropArea.RIGHT
        } else if(inside) {
            CropArea.INSIDE
        } else {
            CropArea.NONE
        }

    }

}
