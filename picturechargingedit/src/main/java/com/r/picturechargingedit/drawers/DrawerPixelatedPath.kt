package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.r.picturechargingedit.model.Changes

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerPixelatedPath(drawerArgs: DrawerArgs): Drawer<Changes>(drawerArgs) {

    private val pathPaint = Paint()


    init {
        pathPaint.color = Color.WHITE
        pathPaint.style = Paint.Style.FILL
        pathPaint.strokeCap = Paint.Cap.SQUARE
    }


    override fun drawChangesOnCanvas(changes: Changes, canvas: Canvas) {
        changes.calculateColors()
        for(model in changes.getColors()) {
            for((i, rect) in model.rectPathModel.getRects().withIndex()) {
                pathPaint.color = model.getColors().getOrNull(i) ?: Color.TRANSPARENT
                canvas.drawRect(rect, pathPaint)
            }
        }
    }


}
