package com.r.picturechargingedit.model.scale

import com.r.picturechargingedit.EditPictureMode

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

abstract class ModeSettable {


    var translatingEnabled = true
    var scalingEnabled = true


    fun setMode(mode: EditPictureMode) {
        when(mode) {
            EditPictureMode.NONE -> setTranslateScaleEnabled(true, true)
            EditPictureMode.PIXELATE -> setTranslateScaleEnabled(false, false)
            EditPictureMode.PIXELATE_VIA_CLICK -> setTranslateScaleEnabled(true, true)
            EditPictureMode.PIXELATE_VIA_DRAG -> setTranslateScaleEnabled(false, true)
            EditPictureMode.CROP -> setTranslateScaleEnabled(false, false, true)
            else -> Unit
        }
    }

    open fun reset() { }


    /**
     * used to dynamically enable/disable translation and calling reset function if flag is set
     */
    private fun setTranslateScaleEnabled(translate: Boolean, scale: Boolean, reset: Boolean = false) {
        translatingEnabled = translate
        scalingEnabled = scale

        if(reset) {
            reset()
        }
    }

}
