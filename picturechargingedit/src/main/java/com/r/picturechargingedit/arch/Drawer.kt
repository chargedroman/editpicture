package com.r.picturechargingedit.arch

import android.graphics.Canvas
import com.r.picturechargingedit.mvp.impl.EditPictureViewArgs

/**
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

abstract class Drawer<T>(val viewArgs: EditPictureViewArgs) {

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
