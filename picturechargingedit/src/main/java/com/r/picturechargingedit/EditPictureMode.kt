package com.r.picturechargingedit

/**
 *
 * Author: romanvysotsky
 * Created: 20.08.20
 */

enum class EditPictureMode {
    NONE,

    PIXELATE,
    PIXELATE_VIA_DRAG,
    PIXELATE_VIA_CLICK,

    CROP;


    fun isPixelation(): Boolean {
        return this == PIXELATE || this == PIXELATE_VIA_DRAG || this == PIXELATE_VIA_CLICK
    }

    fun isCropping(): Boolean {
        return this == CROP
    }

}
