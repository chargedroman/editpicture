package com.r.picturechargingedit.v2

import android.content.Context
import android.net.Uri
import com.r.picturechargingedit.arch.BasePresenter
import com.r.picturechargingedit.io.EditPictureIO
import com.r.picturechargingedit.model.ChangesModel
import io.reactivex.Completable

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPicturePresenter(
    private val originalPicture: Uri,
    private val editIO: EditPictureIO
): BasePresenter<EditPicture>() {

    private var changesModel: ChangesModel = ChangesModel()


    fun undoLastAction(): Boolean {
        val wasUndone = changesModel.undoLastAction()
        getView()?.showChanges(changesModel)
        return wasUndone
    }

    fun editPicture() = Completable.fromAction {
        val bitmap = editIO.readPictureBitmap(originalPicture)
        getView()?.showBitmap(bitmap)
    }

    fun savePicture() = Completable.fromAction {
        val oldChanges = changesModel
        val edited = getView()?.commitChanges(oldChanges) ?: return@fromAction
        changesModel = ChangesModel()
        getView()?.showChanges(changesModel)
        editIO.savePicture(originalPicture, edited)
    }


    fun startRecordingDraw(x: Float, y: Float) {
        changesModel.startRecordingDraw(x, y)
        getView()?.showChanges(changesModel)
    }

    fun continueRecordingDraw(x: Float, y: Float) {
        changesModel.continueRecordingDraw(x, y)
        getView()?.showChanges(changesModel)
    }



    class Factory(private val context: Context) {
        fun create(originalPicture: Uri): EditPicturePresenter {
            val ioUtil = EditPictureIO(context)
            return EditPicturePresenter(originalPicture, ioUtil)
        }
    }

}
