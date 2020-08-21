package com.r.picturechargingedit.mvp

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.BasePresenter
import com.r.picturechargingedit.arch.BaseView
import com.r.picturechargingedit.io.EditPictureIO
import com.r.picturechargingedit.model.ChangesModel
import io.reactivex.Completable

/**
 *
 * Author: romanvysotsky
 * Created: 20.08.20
 */

interface BaseEditPicturePresenter : BasePresenter<BaseEditPictureView> {

    /**
     * api for user
     */

    fun setRectRadius(rectRadius: Float)
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
    fun getBitmap(): Bitmap?
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun startRecordingDraw(x: Float, y: Float)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun continueRecordingDraw(x: Float, y: Float)



    class Factory(private val context: Context) {
        fun create(originalPicture: Uri): BaseEditPicturePresenter {
            val ioUtil = EditPictureIO(context)
            return EditPicturePresenter(originalPicture, ioUtil)
        }
    }

}

interface BaseEditPictureView: BaseView {

    fun setPresenter(presenter: BaseEditPicturePresenter)
    fun getPresenter(): BaseEditPicturePresenter?

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showBitmap(bitmap: Bitmap)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showChanges(changesModel: ChangesModel)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun commitChanges(changesModel: ChangesModel): Bitmap?

}
