package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import com.r.picturechargingedit.model.scale.Scale

/**
 *
 * Author: romanvysotsky
 * Created: 27.08.20
 */

class DrawerScale : Drawer<Scale>() {

    override fun drawChangesOnCanvas(changes: Scale, canvas: Canvas) {
        canvas.setMatrix(changes.getScalingMatrix())
    }

}
