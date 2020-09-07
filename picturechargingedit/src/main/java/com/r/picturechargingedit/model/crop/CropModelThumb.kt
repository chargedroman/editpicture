package com.r.picturechargingedit.model.crop

import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.scale.ScalingMotionEvent

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

class CropModelThumb(pictureModel: Picture) : BaseCrop(pictureModel) {


    override fun canDrawForMode(mode: EditPictureMode): Boolean = mode == EditPictureMode.THUMBNAIL


    override fun cropRectWith(
        area: CropArea,
        lastEvent: ScalingMotionEvent,
        currentEvent: ScalingMotionEvent
    ) {

        val dx = currentEvent.mappedX - lastEvent.mappedX
        val dy = currentEvent.mappedY - lastEvent.mappedY


    }

}
