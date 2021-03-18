package com.r.picturechargingedit.model.crop

import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.model.picture.Picture

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

class CropModelCropCircle(pictureModel: Picture) : CropModelThumb(pictureModel) {


    companion object {
        const val HITBOX_FACTOR = 6f
    }

    override val hitBoxFactor: Float get() = HITBOX_FACTOR


    override fun canDrawForMode(mode: EditPictureMode): Boolean = mode == EditPictureMode.CROP_CIRCLE


    override fun getAspectRatio(): Float {
        return 1f
    }

}
