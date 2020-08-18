package com.r.picturechargingedit

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.view.View

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPictureView : View, EditView {

    private val presenter: EditPicturePresenter = EditPicturePresenter
        .Factory(context.applicationContext)
        .create(this)

    private var pictureBitmap: Bitmap? = null


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            drawPictureBitmap(this, pictureBitmap)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        setMeasuredDimension(width, height)
    }

    override fun showBitmap(bitmap: Bitmap) {
        pictureBitmap = bitmap
        invalidate()
    }

    fun onPictureSelected(picture: Uri) {
        presenter.onPictureSelected(picture)
    }


    private fun drawPictureBitmap(canvas: Canvas, bitmap: Bitmap?) {
        if(bitmap == null) return
        val matrix = Matrix()
        fillSide(matrix, bitmap)
        canvas.drawBitmap(bitmap, matrix, null)
    }

    private fun fillSide(matrix: Matrix, bitmap: Bitmap) {
        val src = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val dest = RectF(0f, 0f, width.toFloat(), height.toFloat())
        matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER)
    }


}
