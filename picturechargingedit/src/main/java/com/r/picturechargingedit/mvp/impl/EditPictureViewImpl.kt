package com.r.picturechargingedit.mvp.impl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.r.picturechargingedit.drawers.*
import com.r.picturechargingedit.model.crop.Crop
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.pixelation.Pixelation
import com.r.picturechargingedit.model.scale.Scale
import com.r.picturechargingedit.mvp.EditPicturePresenter
import com.r.picturechargingedit.mvp.EditPictureView

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

@SuppressLint("ClickableViewAccessibility")
class EditPictureViewImpl : View, EditPictureView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        args = EditPictureViewArgs(context, attrs)
        drawerScale = DrawerScale(args)
        drawerCrop = DrawerCrop(args)
        drawerCropCircle = DrawerCropCircle(args)
        drawerThumbnail = DrawerThumbnail(args)
        drawerPicture = DrawerPicture(args)
        drawerPixelation = DrawerPixelation(args)
    }


    private val args: EditPictureViewArgs
    private val drawerScale: DrawerScale
    private val drawerCrop: DrawerCrop
    private val drawerCropCircle: DrawerCropCircle
    private val drawerThumbnail: DrawerThumbnail
    private val drawerPicture: DrawerPicture
    private val drawerPixelation: DrawerPixelation


    private var presenter: EditPicturePresenter? = null


    override fun setPresenter(presenter: EditPicturePresenter) {
        this.presenter = presenter
        presenter.attach(this)
    }

    override fun getPresenter(): EditPicturePresenter? {
        return presenter
    }


    override fun onDetachedFromWindow() {
        presenter?.detach()
        super.onDetachedFromWindow()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            save()
            drawerScale.onDraw(canvas)
            drawerPicture.onDraw(canvas)
            drawerPixelation.onDraw(canvas)
            drawerCrop.onDraw(canvas)
            drawerCropCircle.onDraw(canvas)
            drawerThumbnail.onDraw(canvas)
            restore()
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        presenter?.onTouchEvent(event)
        return true
    }



    override fun notifyChanged() {
        post(this::invalidate)
    }


    override fun showCrop(cropModel: Crop) {
        drawerCrop.showChanges(cropModel)
    }

    override fun showCropCircle(cropModel: Crop) {
        drawerCropCircle.showChanges(cropModel)
    }

    override fun showThumbnail(cropModel: Crop) {
        drawerThumbnail.showChanges(cropModel)
    }


    override fun showScale(scaleModel: Scale) {
        drawerScale.showChanges(scaleModel)
    }

    override fun showPicture(pictureModel: Picture) {
        drawerPicture.showChanges(pictureModel)
    }

    override fun showPixelation(pixelationModel: Pixelation) {
        drawerPixelation.showChanges(pixelationModel)
    }


    override fun drawPixelation(pixelationModel: Pixelation, canvas: Canvas) {
        drawerPixelation.drawChangesOnCanvas(pixelationModel, canvas)
    }

}
