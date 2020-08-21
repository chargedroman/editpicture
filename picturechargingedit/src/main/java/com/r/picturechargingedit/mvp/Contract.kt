package com.r.picturechargingedit.mvp

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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

    fun setMode(mode: EditPictureMode, clearChanges: Boolean = false)
    fun undoLastAction(undoAll: Boolean = false)

    fun editPicture(): Completable
    fun savePicture(): Completable


    /**
     * called by view
     */
    fun startRecordingDraw(x: Float, y: Float)
    fun continueRecordingDraw(x: Float, y: Float)

    /**
     * live data to observe relevant state
     */
    fun getCanUndo(): LiveData<Boolean>
    fun getMode(): LiveData<EditPictureMode>
    fun getBitmap(): Bitmap?



    class Factory(private val context: Context) {
        fun create(originalPicture: Uri): BaseEditPicturePresenter {
            val ioUtil = EditPictureIO(context)
            return EditPicturePresenter(originalPicture, ioUtil)
        }
    }

}

interface BaseEditPictureView: BaseView {

    fun getPresenter(): BaseEditPicturePresenter?
    fun showBitmap(bitmap: Bitmap)
    fun showChanges(changesModel: ChangesModel)
    fun commitChanges(changesModel: ChangesModel): Bitmap?

}
