package com.r.picturechargingedit.arch

import android.graphics.RectF

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

fun RectF.copyInto(rectF: RectF) {
    rectF.top = top
    rectF.left = left
    rectF.right = right
    rectF.bottom = bottom
}

fun RectF.add(rectF: RectF) {
    top += rectF.top
    left += rectF.left
    right += rectF.right
    bottom += rectF.bottom
}
