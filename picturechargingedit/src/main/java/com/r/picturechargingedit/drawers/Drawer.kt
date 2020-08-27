package com.r.picturechargingedit.drawers

import android.graphics.Canvas

/**
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

abstract class Drawer<T> {

    private var changes: T? = null

    fun showChanges(changes: T) {
        this.changes = changes
    }

    fun onDraw(canvas: Canvas) {
        val changes = changes ?: return
        drawChangesOnCanvas(changes, canvas)
    }

    abstract fun drawChangesOnCanvas(changes: T, canvas: Canvas)

}
