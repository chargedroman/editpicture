package com.r.picturechargingedit.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.r.picturechargingedit.model.ChangesModel
import com.r.picturechargingedit.model.RectColorModel

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class DrawerPixelatedPath(private val drawerArgs: DrawerArgs) {

    private var rectColors = listOf<RectColorModel>()

    private val pathPaint = Paint()


    init {
        pathPaint.color = Color.WHITE
        pathPaint.style = Paint.Style.FILL
        pathPaint.strokeCap = Paint.Cap.SQUARE
    }


    fun drawChangesOnCanvas(changes: ChangesModel, canvas: Canvas) {
        onDraw(changes.getColors(), canvas)
    }

    fun showChanges(changes: ChangesModel) {
        rectColors = changes.getColors()
    }

    fun onDraw(canvas: Canvas) {
        calculateColors()
        onDraw(rectColors, canvas)
    }

    private fun calculateColors() {
        val bitmap = drawerArgs.getCurrentBitmap() ?: return
        val matrix = drawerArgs.createInvertedMatrix()
        for(model in rectColors) {
            model.calculateColors(bitmap, matrix)
        }
    }


    private fun onDraw(rectColors: List<RectColorModel>, canvas: Canvas) {
        for(model in rectColors) {
            for((i, rect) in model.rectPathModel.getRects().withIndex()) {
                pathPaint.color = model.getColors().getOrNull(i) ?: Color.TRANSPARENT
                canvas.drawRect(rect, pathPaint)
            }
        }
    }


}
