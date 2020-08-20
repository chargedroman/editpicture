package com.r.picturechargingedit.mvp

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.Presenter
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
) : Presenter<BaseEditPictureView>(), BaseEditPicturePresenter {


    private val mIsLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mCanUndo: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mMode: MutableLiveData<EditPictureMode> = MutableLiveData(
        EditPictureMode.NONE)

    override fun isLoading() = mIsLoading
    override fun getCanUndo() = mCanUndo
    override fun getMode() = mMode

    private var changesModel: ChangesModel = ChangesModel()
    private val lock = Object()


    /**
     * if there was at least one change applied to the picture by the user, deletes the last change
     */
    override fun undoLastAction() {
        changesModel.undoLastAction()
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }

    /**
     * updates the current operating [Mode]
     */
    override fun setMode(mode: EditPictureMode, clearChanges: Boolean) {
        mMode.postValue(mode)
        if(clearChanges) {
            changesModel.clear()
            getView()?.showChanges(changesModel)
        }
    }

    /**
     * loads the [originalPicture] and presents it
     */
    override fun editPicture() = completable {
        val bitmap = editIO.readPictureBitmap(originalPicture)
        getView()?.showBitmap(bitmap)
    }

    /**
     * applies all changes to the currently loaded bitmap and saves the
     * result to [originalPicture] (while keeping exif data)
     */
    override fun savePicture() = completable {
        val allChanges = changesModel
        val edited = getView()?.commitChanges(allChanges) ?: return@completable
        editIO.savePicture(originalPicture, edited)
        changesModel = ChangesModel()
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }


    override fun startRecordingDraw(x: Float, y: Float) {
        if(mMode.value == EditPictureMode.NONE) return
        changesModel.startRecordingDraw(x, y)
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }

    override fun continueRecordingDraw(x: Float, y: Float) {
        if(mMode.value == EditPictureMode.NONE) return
        changesModel.continueRecordingDraw(x, y)
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }


    private fun updateCanUndo() {
        mCanUndo.postValue(changesModel.size() > 0)
    }



    /**
     * utility to handling loading progress
     */
    private fun completable(block: () -> Unit): Completable = Completable.fromAction {
        synchronized(lock) {
            mIsLoading.postValue(true)
            block()
        }
    }.doFinally { mIsLoading.postValue(false) }


}
