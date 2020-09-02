package com.r.picturechargingedit.mvp.impl

import android.net.Uri
import android.view.MotionEvent
import androidx.core.graphics.toRect
import androidx.lifecycle.MutableLiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.Presenter
import com.r.picturechargingedit.model.crop.Crop
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
    private val cropModel: Crop
) : Presenter<EditPictureView>(), EditPicturePresenter {

    companion object {
        private const val THUMBNAIL_ASPECT_RATIO = 3/5f
    }


    private val mCanUndo: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mMode: MutableLiveData<EditPictureMode> = MutableLiveData(EditPictureMode.NONE)
    private val lock = Object()
    private var thumbnailAspectRatio = THUMBNAIL_ASPECT_RATIO

    override fun getCanUndo() = mCanUndo
    override fun getMode() = mMode


    /**
     * if there was at least one change applied to the picture by the user, deletes the last change
     */
    override fun undoLastAction(undoAll: Boolean) {
        if(undoAll) {
            pixelationModel.clear()
        } else {
            pixelationModel.removeLast()
        }
        getView()?.notifyChanged()
        updateCanUndo()
    }


    /**
     * updates the current operating [EditPictureMode]
     */
    override fun setMode(mode: EditPictureMode, clearChanges: Boolean) {
        mMode.postValue(mode)
        scaleModel.setMode(mode)
        scaleTouchModel.setMode(mode)
        cropModel.setMode(mode, mode.aspectRatio())
        if(clearChanges) {
            pixelationModel.clear()
            cropModel.clear()
        }
        getView()?.notifyChanged()
    }

    override fun setThumbnailAspectRatio(aspectRatio: Float) {
        this.thumbnailAspectRatio = aspectRatio
        val mode = getMode().value ?: return
        cropModel.setMode(mode, aspectRatio)
        getView()?.notifyChanged()
    }


    /**
     * loads the [originalPicture] and presents it
     */
    override fun editPicture() = completable {
        val bitmap = editIO.readPictureBitmap(originalPicture)
        pixelationModel.clear()
        cropModel.clear()
        pictureModel.setBitmap(bitmap)
        getView()?.notifyChanged()
    }

    /**
     * applies all changes to the currently loaded bitmap and saves the
     * result to [originalPicture] (while keeping exif data)
     */
    override fun savePicture() = completable {

        val view = getView()
        val bitmapCanvas = pictureModel.createBitmapCanvas()

        if(pixelationModel.getSize() == 0 || view == null || bitmapCanvas == null) {
            return@completable
        }

        pixelationModel.invertAllCoordinates()
        view.drawPixelation(pixelationModel, bitmapCanvas)
        pixelationModel.clear()
        cropModel.clear()

        val edited = pictureModel.getBitmap() ?: throw NullPointerException("Edited Bitmap null")
        pictureModel.setBitmap(edited)

        val originalExif = editIO.readExif(originalPicture)
        editIO.savePicture(originalPicture, edited, originalExif)

        getView()?.notifyChanged()
        updateCanUndo()
    }


    override fun cropPicture() = completable {
        val bitmap = pictureModel.getBitmap()
        if(!cropModel.canDrawCrop() || bitmap == null)
            throw IllegalArgumentException("Can't crop image.")

        val cropRect = cropModel.getCroppingRect()
        pictureModel.getMatrixInverted().mapRect(cropRect)
        val rect = cropRect.toRect()

        savePicture().blockingAwait()
        val originalExif = editIO.readExif(originalPicture)
        val edited = editIO.cropBitmap(bitmap, rect)
        editIO.savePicture(originalPicture, edited, originalExif)
        editPicture().blockingAwait()

        getView()?.notifyChanged()
    }

    override fun createThumbnail(thumbnailUri: Uri) = completable {
        val bitmap = pictureModel.getBitmap()
        if(!cropModel.canDrawCrop() || bitmap == null)
            throw IllegalArgumentException("Can't create thumbnail.")

        val cropRect = cropModel.getCroppingRect()
        pictureModel.getMatrixInverted().mapRect(cropRect)
        val rect = cropRect.toRect()
        val edited = editIO.cropBitmap(bitmap, rect)
        editIO.savePicture(thumbnailUri, edited)
        cropModel.clear()

        getView()?.notifyChanged()
    }


    override fun attach(view: EditPictureView) {
        super.attach(view)
        view.showPicture(pictureModel)
        view.showScale(scaleModel)
        view.showPixelation(pixelationModel)
        view.showCrop(cropModel)
        view.notifyChanged()
    }


    override fun onTouchEvent(event: MotionEvent) {
        scaleModel.onTouchEvent(event)
        scaleTouchModel.onTouchEvent(event, this::onTouchEventScaled)
        getView()?.notifyChanged()
    }


    private fun onTouchEventScaled(event: ScalingMotionEvent) {
        val mode = mMode.value ?: return

        if(mode.isPixelation()) {
            when(event.interaction) {
                ScalingInteraction.CLICK -> startRecordingPixelation(event.mappedX, event.mappedY, event.mappedMargin)
                ScalingInteraction.MOVE -> continueRecordingPixelation(event.mappedX, event.mappedY, event.mappedMargin)
                else -> Unit
            }
        } else if(mode.isCropping()) {
            cropModel.onTouchEvent(event)
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
    private fun completable(block: () -> Unit) = Completable.fromAction {
        synchronized(lock) {
            block()
        }
    }


    private fun EditPictureMode.aspectRatio(): Float {
        return if(this == EditPictureMode.THUMBNAIL) {
            thumbnailAspectRatio
        } else {
            1f
        }
    }


}
