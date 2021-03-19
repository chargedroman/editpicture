package com.r.picturechargingedit.model.crop

import android.graphics.RectF
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.copyInto
import com.r.picturechargingedit.model.picture.Picture
import kotlin.math.abs

/**
 *
 * Author: romanvysotsky
 * Created: 03.09.20
 */

class CropModelCropCircle(pictureModel: Picture) : CropModelThumb(pictureModel) {


    companion object {
        const val HITBOX_FACTOR = 5f
    }

    override val hitBoxFactor: Float get() = HITBOX_FACTOR

    override fun canDrawForMode(mode: EditPictureMode): Boolean =
        mode == EditPictureMode.CROP_CIRCLE


    override fun getAspectRatio(): Float {
        return 1f
    }

    /**
     * updates [bufferRect] in such a way, that its a square and in the center of [originalBoundsRect].
     * Makes sure that the crop circle is centered initially
     */
    override fun updateInitialBoundsToBufferRect(bufferRect: RectF) {
        val maxWidth = originalBoundsRect.width()
        val maxHeight = originalBoundsRect.height()
        val diff = abs(maxWidth - maxHeight) /2
        originalBoundsRect.copyInto(bufferRect)

        if(maxWidth < maxHeight) {
            bufferRect.bottom = (bufferRect.bottom - diff).coerceAtLeast(0f)
            bufferRect.top = (bufferRect.top + diff).coerceAtMost(bufferRect.bottom)
        } else {
            bufferRect.right = (bufferRect.right - diff).coerceAtLeast(0f)
            bufferRect.left = (bufferRect.left + diff).coerceAtMost(bufferRect.right)
        }
    }

}
