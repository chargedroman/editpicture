package com.r.picturechargingedit.mvp

import android.content.Context
import android.net.Uri
import android.view.MotionEvent
import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.BasePresenter
import com.r.picturechargingedit.model.crop.CropModelCrop
import com.r.picturechargingedit.model.crop.CropModelThumb
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

    fun undoLastAction(undoAll: Boolean = false)
    fun setMode(mode: EditPictureMode, clearChanges: Boolean = false)
    fun setThumbnailParams(aspectRatio: Float, quality: Int)

    fun getCanUndo(): LiveData<Boolean>
    fun getMode(): LiveData<EditPictureMode>

    fun editPicture(): Completable
    fun savePicture(): Completable
    fun cropPicture(): Completable
    fun createThumbnail(): Completable


    /**
     * called by view
     */

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun onTouchEvent(event: MotionEvent)



    class Factory(private val context: Context) {

        fun create(originalPicture: Uri, thumbnailUri: Uri): EditPicturePresenter {

            val ioUtil = EditPictureIO.create(context)
            val pictureModel = PictureModel()
            val scaleModel = ScaleModel(pictureModel)
            val scaleTouchModel = ScaleTouchModel(pictureModel, scaleModel)
            val pixelationModel = PixelationModel(pictureModel)
            val cropModel = CropModelCrop(pictureModel)
            val thumbnailModel = CropModelThumb(pictureModel)

            return EditPicturePresenterImpl(
                originalPicture,
                thumbnailUri,
                ioUtil,
                pictureModel,
                scaleModel,
                scaleTouchModel,
                pixelationModel,
                cropModel,
                thumbnailModel
            )
        }

    }

}
