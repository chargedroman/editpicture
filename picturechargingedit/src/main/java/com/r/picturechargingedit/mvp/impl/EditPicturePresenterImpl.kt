package com.r.picturechargingedit.mvp.impl

import android.graphics.Rect
import android.net.Uri
import android.view.MotionEvent
import androidx.core.graphics.toRect
import androidx.lifecycle.MutableLiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.Presenter
import com.r.picturechargingedit.model.crop.Crop
import com.r.picturechargingedit.model.crop.ThumbnailDimensions
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.pixelation.Pixelation
import com.r.picturechargingedit.model.scale.Scale
import com.r.picturechargingedit.model.scale.ScalingInteraction
import com.r.picturechargingedit.model.scale.ScalingMotionEvent
import com.r.picturechargingedit.model.scale.touch.ScaleTouch
import com.r.picturechargingedit.mvp.EditPicturePresenter
import com.r.picturechargingedit.mvp.EditPictureView
import com.r.picturechargingedit.util.EditPictureIO
import io.reactivex.Completable

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPicturePresenterImpl(
    private val originalPicture: Uri,
    private val editIO: EditPictureIO,
    private val pictureModel: Picture,
    private val scaleModel: Scale,
    private val scaleTouchModel: ScaleTouch,
    private val pixelationModel: Pixelation,
    private val cropModel: Crop,
    private val thumbnailModel: Crop
) : Presenter<EditPictureView>(), EditPicturePresenter {


    private val mCanUndo: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mCanUndoCrop: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mMode: MutableLiveData<EditPictureMode> = MutableLiveData(EditPictureMode.NONE)
    private val mLock = Object()

    override fun getCanUndo() = mCanUndo
    override fun getMode() = mMode
    override fun getCanUndoCrop() = mCanUndoCrop


    /**
     * if there was at least one change applied to the picture by the user, deletes the last change
     */
    override fun undoLastAction() {
        pixelationModel.removeLast()
        getView()?.notifyChanged()
        updateCanUndo()
    }


    /**
     * updates the current operating [EditPictureMode]
     */
    override fun setMode(mode: EditPictureMode) {
        mMode.postValue(mode)
        scaleModel.setMode(mode)
        scaleTouchModel.setMode(mode)
        cropModel.setMode(mode)
        thumbnailModel.setMode(mode)
        getView()?.notifyChanged()
    }


    /**
     * sets [aspectRatio] of [thumbnailModel]
     */
    override fun setThumbnailAspectRatio(aspectRatio: Float) {
        val mode = getMode().value ?: return
        thumbnailModel.setAspectRatio(aspectRatio)
        thumbnailModel.setMode(mode)
        getView()?.notifyChanged()
    }

    /**
     * sets [minimumWidth] of [thumbnailModel] and [cropModel]
     */
    override fun setMinimumWidth(minimumWidth: Float) {
        val mode = getMode().value ?: return
        thumbnailModel.setMinWidth(minimumWidth)
        thumbnailModel.setMode(mode)
        cropModel.setMinWidth(minimumWidth)
        cropModel.setMode(mode)
        getView()?.notifyChanged()
    }


    /**
     * loads the [originalPicture] and presents it
     */
    override fun editPicture(): Completable = call {
        val bitmap = editIO.readPictureBitmap(originalPicture)
        editIO.downSample(originalPicture, editIO.getBackupLocation())

        pictureModel.setBitmap(bitmap)
        pixelationModel.clear()
        cropModel.clear()
        thumbnailModel.clear()
        mCanUndoCrop.postValue(false)

        updateCanUndo()
        getView()?.notifyChanged()
    }


    /**
     * resets the to the picture which is saved in the backup location uri of [editIO]
     */
    override fun resetChanges(): Completable = call {

        editIO.downSample(editIO.getBackupLocation(), originalPicture)
        val bitmap = editIO.readPictureBitmap(editIO.getBackupLocation())

        pixelationModel.clear()
        cropModel.clear()
        thumbnailModel.clear()
        pictureModel.setBitmap(bitmap)
        mCanUndoCrop.postValue(false)

        getView()?.notifyChanged()

    }


    /**
     * applies all changes to the currently loaded bitmap and saves the
     * result to [originalPicture] (while keeping exif data)
     */
    override fun savePicture(): Completable = call {

        val view = getView()
        val bitmap = pictureModel.getBitmap()
        val bitmapCanvas = pictureModel.createBitmapCanvas()

        if(view == null || bitmap == null || bitmapCanvas == null) {
            throw IllegalArgumentException("Can't save image.")
        }

        pixelationModel.mapCoordinatesInverted()
        view.drawPixelation(pixelationModel, bitmapCanvas)

        val originalExif = editIO.readExif(originalPicture)
        editIO.savePicture(originalPicture, bitmap, originalExif)

        mCanUndoCrop.postValue(false)
        pixelationModel.clear()

    }


    /**
     * applies the current pixelation to [originalPicture]
     * then crops the [originalPicture] and saves the result to [originalPicture]
     */
    override fun cropPicture(): Completable = call {

        if(!cropModel.hasChanges()) return@call

        val bitmap = pictureModel.getBitmap()

        if(!cropModel.canDraw() || bitmap == null)
            throw IllegalArgumentException("Can't crop image.")

        val cropRect = cropModel.getCroppingRect()
        pictureModel.getMatrixInverted().mapRect(cropRect)
        val rect = cropRect.toRect()
        pictureModel.getMatrix().mapRect(cropRect)

        val originalExif = editIO.readExif(originalPicture)
        val edited = editIO.cropBitmap(bitmap, rect)
        editIO.savePicture(originalPicture, edited, originalExif)
        pictureModel.setBitmap(edited)

        pixelationModel.mapCoordinatesTo(rect)
        cropModel.clear()
        thumbnailModel.clear()

        mCanUndoCrop.postValue(true)
        getView()?.notifyChanged()
    }


    /**
     * creates a thumbnail from the current [thumbnailModel]
     * applies the current pixelation and saves the result to [thumbnailUri]
     */
    override fun createThumbnail(thumbnailUri: Uri): Completable = call {

        val view = getView()
        val bitmap = pictureModel.getBitmap()
        val bitmapCanvas = pictureModel.createBitmapCanvas()

        if(!thumbnailModel.canDraw() || bitmap == null || view == null || bitmapCanvas == null)
            throw IllegalArgumentException("Can't create thumbnail.")

        pixelationModel.mapCoordinatesInverted()
        view.drawPixelation(pixelationModel, bitmapCanvas)
        pixelationModel.mapCoordinates()
        val originalBitmap = editIO.readPictureBitmap(originalPicture)
        pictureModel.setBitmap(originalBitmap)

        val cropRect = thumbnailModel.getCroppingRect()
        pictureModel.getMatrixInverted().mapRect(cropRect)
        val rect = cropRect.toRect()
        pictureModel.getMatrix().mapRect(cropRect)

        val edited = editIO.cropBitmap(bitmap, rect)
        editIO.savePicture(thumbnailUri, edited, 100)

    }

    override fun createThumbnailDimensions(): ThumbnailDimensions? = synchronized(mLock) {

        val bitmap = pictureModel.getBitmap() ?: return null
        val originalRect = Rect(0,0, bitmap.width, bitmap.height)

        val cropRect = thumbnailModel.getCroppingRect()
        pictureModel.getMatrixInverted().mapRect(cropRect)
        val thumbnailRect = cropRect.toRect()
        pictureModel.getMatrix().mapRect(cropRect)

        thumbnailRect.right = thumbnailRect.right.coerceAtMost(bitmap.width)
        thumbnailRect.bottom = thumbnailRect.bottom.coerceAtMost(bitmap.height)
        thumbnailRect.top = thumbnailRect.top.coerceAtLeast(0)
        thumbnailRect.left = thumbnailRect.left.coerceAtLeast(0)

        return ThumbnailDimensions(originalRect, thumbnailRect)
    }


    override fun attach(view: EditPictureView) {
        super.attach(view)
        view.showPicture(pictureModel)
        view.showScale(scaleModel)
        view.showPixelation(pixelationModel)
        view.showCrop(cropModel)
        view.showThumbnail(thumbnailModel)
        view.notifyChanged()
    }


    override fun onTouchEvent(event: MotionEvent) {
        scaleModel.onTouchEvent(event)
        scaleTouchModel.onTouchEvent(event, this::onTouchEventScaled)
        getView()?.notifyChanged()
    }


    private fun onTouchEventScaled(event: ScalingMotionEvent) {
        val mode = mMode.value ?: return

        when {
            mode.isPixelation() ->
                when(event.interaction) {
                    ScalingInteraction.CLICK -> startRecordingPixelation(event.mappedX, event.mappedY, event.mappedMargin)
                    ScalingInteraction.MOVE -> continueRecordingPixelation(event.mappedX, event.mappedY, event.mappedMargin)
                    else -> Unit
                }
            mode == EditPictureMode.CROP -> cropModel.onTouchEvent(event)
            mode == EditPictureMode.THUMBNAIL -> thumbnailModel.onTouchEvent(event)
        }

        getView()?.notifyChanged()
    }


    private fun startRecordingPixelation(x: Float, y: Float, radius: Float) {
        pixelationModel.startRecordingDraw(x, y, radius)
        getView()?.notifyChanged()
        updateCanUndo()
    }

    private fun continueRecordingPixelation(x: Float, y: Float, radius: Float) {
        if(mMode.value == EditPictureMode.PIXELATE_VIA_CLICK) return

        pixelationModel.continueRecordingDraw(x, y, radius)
        getView()?.notifyChanged()
        updateCanUndo()
    }


    private fun updateCanUndo() {
        mCanUndo.postValue(pixelationModel.getSize() > 0)
    }


    /**
     * utility to synchronize access to io operations on [originalPicture]
     */
    private fun call(block: () -> Unit) = Completable.fromAction {
        synchronized(mLock) { block() }
    }


}
