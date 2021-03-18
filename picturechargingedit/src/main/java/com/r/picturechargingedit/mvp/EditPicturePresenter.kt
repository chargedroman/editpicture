package com.r.picturechargingedit.mvp

import android.content.Context
import android.net.Uri
import android.view.MotionEvent
import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.BasePresenter
import com.r.picturechargingedit.model.crop.CropModelCrop
import com.r.picturechargingedit.model.crop.CropModelCropCircle
import com.r.picturechargingedit.model.crop.CropModelThumb
import com.r.picturechargingedit.model.crop.ThumbnailDimensions
import com.r.picturechargingedit.model.picture.PictureModel
import com.r.picturechargingedit.model.pixelation.PixelationModel
import com.r.picturechargingedit.model.scale.ScaleModel
import com.r.picturechargingedit.model.scale.touch.ScaleTouchModel
import com.r.picturechargingedit.mvp.impl.EditPicturePresenterImpl
import com.r.picturechargingedit.util.EditPictureIO
import io.reactivex.Completable

/**
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

interface EditPicturePresenter : BasePresenter<EditPictureView> {

    /**
     * api for user
     */

    fun undoLastBlur()
    fun undoLastCropPosition()
    fun setMode(mode: EditPictureMode)
    fun setMinimumWidth(minimumWidth: Float)

    fun getMode(): LiveData<EditPictureMode>
    fun getCanUndoBlur(): LiveData<Boolean>
    fun getCanUndoCropPosition(): LiveData<Boolean>
    fun getCanUndoCircleCropPosition(): LiveData<Boolean>
    fun getCanResetChanges(): LiveData<Boolean>

    fun resetChanges(): Completable
    fun cropPicture(): Completable
    fun editPicture(sizeMax: Int = EditPictureIO.SIZE_4_K): Completable
    fun savePicture(quality: Int = EditPictureIO.QUALITY_MAX): Completable

    fun createThumbnail(thumbnailUri: Uri): Completable
    fun createThumbnailDimensions(): ThumbnailDimensions?
    fun setThumbnailAspectRatio(aspectRatio: Float)


    /**
     * called by view
     */

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun onTouchEvent(event: MotionEvent)



    class Factory(private val context: Context) {

        fun create(originalPicture: Uri): EditPicturePresenter {

            val ioUtil = EditPictureIO.create(context)
            val pictureModel = PictureModel()
            val scaleModel = ScaleModel(pictureModel)
            val scaleTouchModel = ScaleTouchModel(pictureModel, scaleModel)
            val pixelationModel = PixelationModel(pictureModel)
            val cropModel = CropModelCrop(pictureModel)
            val cropModelCircle = CropModelCropCircle(pictureModel)
            val thumbnailModel = CropModelThumb(pictureModel)

            return EditPicturePresenterImpl(
                originalPicture,
                ioUtil,
                pictureModel,
                scaleModel,
                scaleTouchModel,
                pixelationModel,
                cropModel,
                cropModelCircle,
                thumbnailModel
            )
        }

    }

}
