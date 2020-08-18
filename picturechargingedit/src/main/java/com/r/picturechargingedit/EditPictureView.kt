package com.r.picturechargingedit

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.doOnNextLayout
import com.r.picturechargingedit.drawers.DrawerBlurPath
import com.r.picturechargingedit.drawers.DrawerBitmap


/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

@SuppressLint("ClickableViewAccessibility")
class EditPictureView : View, EditView {

    private val presenter: EditPicturePresenter = EditPicturePresenter
        .Factory(context.applicationContext)
        .create(this)

    private val drawerBitmap = DrawerBitmap(this)
    private val drawerBlur = DrawerBlurPath(this)


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


    fun onPictureSelected(picture: Uri) = doOnNextLayout {
        presenter.onPictureSelected(picture, width, height)
    }

    fun undoLastAction() {
        drawerBlur.removeLastBlurPath()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            drawerBitmap.drawPictureBitmap(this)
            drawerBlur.drawBlurPath(this)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        setMeasuredDimension(width, height)
    }

    override fun showBitmap(bitmap: Bitmap) {
        drawerBitmap.onNextBitmap(bitmap)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event == null) {
            return super.onTouchEvent(event)
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawerBlur.startRecordingDraw(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                drawerBlur.continueRecordingDraw(event.x, event.y)
            }
        }

        return true
    }


}
