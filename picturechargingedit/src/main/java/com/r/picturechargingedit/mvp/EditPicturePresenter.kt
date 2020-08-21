package com.r.picturechargingedit.mvp

import android.graphics.Bitmap
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

    companion object {
        const val RECT_RADIUS = 20f
    }


    private val mCanUndo: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mMode: MutableLiveData<EditPictureMode> = MutableLiveData(EditPictureMode.NONE)
    private var mBitmap: Bitmap? = null

    override fun getCanUndo() = mCanUndo
    override fun getMode() = mMode
    override fun getBitmap() = mBitmap

    private var changesModel: ChangesModel = ChangesModel(RECT_RADIUS)
    private val lock = Object()


    /**
     * if there was at least one change applied to the picture by the user, deletes the last change
     */
    override fun undoLastAction(undoAll: Boolean) {
        if(undoAll) {
            changesModel.clear()
        } else {
            changesModel.removeLast()
        }
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }

    /**
     * updates the current operating [EditPictureMode]
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
        mBitmap = bitmap
        changesModel.clear()
        getView()?.showBitmap(bitmap)
    }

    /**
     * applies all changes to the currently loaded bitmap and saves the
     * result to [originalPicture] (while keeping exif data)
     */
    override fun savePicture() = completable {

        if(changesModel.size() == 0) {
            return@completable
        }

        val allChanges = changesModel
        changesModel = ChangesModel(RECT_RADIUS)

        val edited = getView()?.commitChanges(allChanges) ?: return@completable
        editIO.savePicture(originalPicture, edited)
        getView()?.showBitmap(edited)
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

    override fun attach(view: BaseEditPictureView) {
        super.attach(view)
        val bitmap = mBitmap ?: return
        view.showBitmap(bitmap)
    }


    private fun updateCanUndo() {
        mCanUndo.postValue(changesModel.size() > 0)
    }



    /**
     * utility to synchronize opening a new picture for editing and saving it
     */
    private fun completable(block: () -> Unit): Completable = Completable.fromAction {
        synchronized(lock) {
            block()
        }
    }


}
