package com.r.picturechargingedit.mvp

import android.content.Context
import android.net.Uri
import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.BasePresenter
import com.r.picturechargingedit.model.ChangesModel
import com.r.picturechargingedit.model.PictureModel
import com.r.picturechargingedit.util.EditPictureIO
import io.reactivex.Completable

/**
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

interface BaseEditPicturePresenter : BasePresenter<BaseEditPictureView> {

    /**
     * api for user
     */

    fun setRectRadius(rectRadius: Float)
    fun setMode(mode: EditPictureMode, clearChanges: Boolean = false)

    fun getRectRadius(): Float
    fun getCanUndo(): LiveData<Boolean>
    fun getMode(): LiveData<EditPictureMode>

    fun undoLastAction(undoAll: Boolean = false)

    fun editPicture(): Completable
    fun savePicture(): Completable


    /**
     * called by view
     */

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun startRecordingDraw(x: Float, y: Float)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun continueRecordingDraw(x: Float, y: Float)



    class Factory(private val context: Context) {
        fun create(originalPicture: Uri): BaseEditPicturePresenter {
            val ioUtil = EditPictureIO.create(context)
            val pictureModel = PictureModel()
            val factory: (Float) -> ChangesModel = { ChangesModel(pictureModel, it) }
            return EditPicturePresenter(originalPicture, ioUtil, factory)
        }
    }

}
