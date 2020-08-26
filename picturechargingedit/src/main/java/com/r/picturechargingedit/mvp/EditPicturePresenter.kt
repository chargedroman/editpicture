package com.r.picturechargingedit.mvp

import android.content.Context
import android.net.Uri
import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.BasePresenter
import com.r.picturechargingedit.model.changes.ChangesModel
import com.r.picturechargingedit.model.picture.PictureModel
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

    fun setRectRadiusFactor(rectRadiusFactor: Float)
    fun getRectRadiusFactor(): Float
    fun getRectRadius(): Float
    fun setMode(mode: EditPictureMode, clearChanges: Boolean = false)

    fun getCanUndo(): LiveData<Boolean>
    fun getMode(): LiveData<EditPictureMode>

    fun undoLastAction(undoAll: Boolean = false)

    fun editPicture(): Completable
    fun savePicture(): Completable


    /**
     * called by view
     */

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun startRecordingDraw(x: Float, y: Float, radius: Float)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun continueRecordingDraw(x: Float, y: Float, radius: Float)



    class Factory(private val context: Context) {
        fun create(originalPicture: Uri): EditPicturePresenter {
            val ioUtil = EditPictureIO.create(context)
            val pictureModel = PictureModel()
            val factory: (Float) -> ChangesModel = { ChangesModel(pictureModel, it) }
            return EditPicturePresenterImpl(
                originalPicture,
                ioUtil,
                factory
            )
        }
    }

}
