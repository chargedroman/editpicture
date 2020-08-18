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
import androidx.lifecycle.Observer
import com.r.picturechargingedit.drawers.DrawerPathModel
import com.r.picturechargingedit.drawers.DrawerBitmap
import com.r.picturechargingedit.model.EditPictureResultArgs
import com.r.picturechargingedit.model.PathModel


/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

@SuppressLint("ClickableViewAccessibility")
class EditPictureView : View {

    private val viewModel: EditPictureViewModel = EditPictureViewModel
        .Factory(context.applicationContext)
        .create()

    private val drawerBitmap = DrawerBitmap(this)
    private val drawerPathModels = DrawerPathModel(this)


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    val onNextBitmap = Observer<Bitmap> {
        drawerBitmap.onNextBitmap(it)
    }

    val onNextFullSizeBitmap = Observer<Bitmap> {
        val canvas = Canvas(it)
        drawerPathModels.drawBlurPath(canvas)
        viewModel.onUserEditingWasCopiedTo(it)
    }

    val onNextPathModels = Observer<List<PathModel>> {
        drawerPathModels.onNextPathModels(it)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewModel.resizedBitmap.observeForever(onNextBitmap)
        viewModel.pathModels.observeForever(onNextPathModels)
        viewModel.fullSizeBitmap.observeForever(onNextFullSizeBitmap)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewModel.resizedBitmap.removeObserver(onNextBitmap)
        viewModel.pathModels.removeObserver(onNextPathModels)
        viewModel.fullSizeBitmap.removeObserver(onNextFullSizeBitmap)
    }


    fun onPictureSelected(picture: Uri) = doOnNextLayout {
        viewModel.onPictureSelected(picture, width, height)
    }

    fun undoLastAction() {
        viewModel.undoLastAction()
    }

    fun onSaveResultClicked(finishEditing: EditPictureResultArgs) {
        viewModel.onSaveResultClicked(finishEditing)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            drawerBitmap.drawPictureBitmap(this)
            drawerPathModels.drawBlurPath(this)
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event == null) {
            return super.onTouchEvent(event)
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                viewModel.startRecordingDraw(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                viewModel.continueRecordingDraw(event.x, event.y)
            }
        }

        return true
    }


}
