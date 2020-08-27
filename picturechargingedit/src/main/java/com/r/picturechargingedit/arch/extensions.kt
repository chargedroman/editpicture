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

fun RectF.setZero() {
    top = 0f
    left = 0f
    right = 0f
    bottom = 0f
}

fun RectF.isZero(): Boolean {
    return top == 0f &&
    left == 0f &&
    right == 0f &&
    bottom == 0f
}