package com.r.picturechargingedit.v2

import android.content.Context
import android.net.Uri
import com.r.picturechargingedit.arch.BasePresenter
import com.r.picturechargingedit.io.EditPictureIO
import com.r.picturechargingedit.model.ChangesModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPicturePresenter(
    private val originalPicture: Uri,
    private val editIO: EditPictureIO
): BasePresenter<EditPicture>() {

    private val changesModel: ChangesModel = ChangesModel()


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
        val bitmap = getView()?.getShownBitmap() ?: return@fromAction
        editIO.savePicture(originalPicture, bitmap)
    }

    fun rotate(degrees: Float) = Completable.fromAction {
        val bitmap = getView()?.getShownBitmap() ?: return@fromAction
        val rotated = editIO.rotate(bitmap, degrees)
        getView()?.showBitmap(rotated)
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
