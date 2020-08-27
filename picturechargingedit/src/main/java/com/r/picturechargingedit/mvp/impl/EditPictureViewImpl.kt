package com.r.picturechargingedit.mvp.impl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.drawers.DrawerPicture
import com.r.picturechargingedit.drawers.DrawerPixelation
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.pixelation.Pixelation
import com.r.picturechargingedit.mvp.EditPicturePresenter
import com.r.picturechargingedit.mvp.EditPictureView
import com.r.picturechargingedit.scale.ScalingInteraction
import com.r.picturechargingedit.scale.ScalingMotionEvent
import com.r.picturechargingedit.scale.ScalingView

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

@SuppressLint("ClickableViewAccessibility")
class EditPictureViewImpl : ScalingView,
    EditPictureView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val drawerPicture = DrawerPicture()
    private val drawerPixelation = DrawerPixelation()

    private var presenter: EditPicturePresenter? = null


    override fun setPresenter(presenter: EditPicturePresenter) {
        this.presenter = presenter
        presenter.attach(this)
    }

    override fun getPresenter(): EditPicturePresenter? {
        return presenter
    }

    override fun showMode(mode: EditPictureMode) {
        when(mode) {
            EditPictureMode.NONE -> setTranslateScaleEnabled(true, true)
            EditPictureMode.PIXELATE -> setTranslateScaleEnabled(false, false)
            EditPictureMode.PIXELATE_VIA_CLICK -> setTranslateScaleEnabled(true, true)
            EditPictureMode.PIXELATE_VIA_DRAG -> setTranslateScaleEnabled(false, true)
            else -> Unit
        }
        invalidate()
    }


    override fun onDetachedFromWindow() {
        presenter?.detach()
        super.onDetachedFromWindow()
    }


    override fun onDrawScaled(canvas: Canvas) {
        drawerPicture.onDraw(canvas)
        drawerPixelation.onDraw(canvas)
    }

    override fun onTouchEventScaled(event: ScalingMotionEvent) {
        val radius = presenter?.getRectRadius() ?: return
        val matrix = getInvertedScalingMatrix()
        val mappedRadius = matrix.mapRadius(radius)

        when(event.interaction) {
            ScalingInteraction.CLICK -> presenter?.startRecordingDraw(event.mappedX, event.mappedY, mappedRadius)
            ScalingInteraction.MOVE -> presenter?.continueRecordingDraw(event.mappedX, event.mappedY, mappedRadius)
            else -> Unit
        }
    }


    override fun showPicture(pictureModel: Picture) {
        drawerPicture.showChanges(pictureModel)
        post(this::invalidate)
    }

    override fun showPixelation(pixelationModel: Pixelation) {
        drawerPixelation.showChanges(pixelationModel)
        post(this::invalidate)
    }

    override fun drawPixelation(pictureModel: Picture, pixelationModel: Pixelation): Bitmap? {
        val canvas = pictureModel.createBitmapCanvas() ?: return null
        pixelationModel.invertAllCoordinates()
        drawerPixelation.drawChangesOnCanvas(pixelationModel, canvas)
        return pictureModel.getBitmap()
    }

}
