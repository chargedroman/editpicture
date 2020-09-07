package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import com.r.picturechargingedit.arch.Drawer
import com.r.picturechargingedit.model.scale.Scale
import com.r.picturechargingedit.mvp.impl.EditPictureViewArgs

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

class DrawerScale(viewArgs: EditPictureViewArgs) : Drawer<Scale>(viewArgs) {

    override fun drawChangesOnCanvas(changes: Scale, canvas: Canvas) {
        canvas.setMatrix(changes.getScalingMatrix())
    }

}
