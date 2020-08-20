package com.r.picturechargingedit.v2

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
) : BasePresenter<EditPicture>() {

    val isLoading: LiveData<Boolean> get() = mIsLoading
    val canUndo: LiveData<Boolean> get() = mCanUndo


    private val mIsLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mCanUndo: MutableLiveData<Boolean> = MutableLiveData(false)

    private var changesModel: ChangesModel = ChangesModel()
    private val lock = Object()


    /**
     * if there was at least one change applied to the picture by the user, deletes the last change
     */
    fun undoLastAction() {
        changesModel.undoLastAction()
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }

    /**
     * loads the [originalPicture] and presents it
     */
    fun editPicture() = completable {
        val bitmap = editIO.readPictureBitmap(originalPicture)
        getView()?.showBitmap(bitmap)
    }

    /**
     * applies all changes to the currently loaded bitmap and saves the
     * result to [originalPicture] (while keeping exif data)
     */
    fun savePicture() = completable {
        val allChanges = changesModel
        val edited = getView()?.commitChanges(allChanges) ?: return@completable
        editIO.savePicture(originalPicture, edited)
        changesModel = ChangesModel()
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }


    fun startRecordingDraw(x: Float, y: Float) {
        changesModel.startRecordingDraw(x, y)
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }

    fun continueRecordingDraw(x: Float, y: Float) {
        changesModel.continueRecordingDraw(x, y)
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }


    private fun updateCanUndo() {
        mCanUndo.postValue(changesModel.size() > 0)
    }


    /**
     * utility to handle loading progress
     */
    private fun completable(block: () -> Unit): Completable = Completable.fromAction {
        synchronized(lock) {
            mIsLoading.postValue(true)
            block()
        }
    }.doFinally { mIsLoading.postValue(false) }



    class Factory(private val context: Context) {
        fun create(originalPicture: Uri): EditPicturePresenter {
            val ioUtil = EditPictureIO(context)
            return EditPicturePresenter(originalPicture, ioUtil)
        }
    }

}
