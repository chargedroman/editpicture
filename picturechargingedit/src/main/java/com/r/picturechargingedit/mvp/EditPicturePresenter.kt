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
     * [rectRadius] the radius which defines the size of the pixelated rects
     */
    override fun setRectRadius(rectRadius: Float) {
        changesModel.setRectRadius(rectRadius)
        getView()?.showChanges(changesModel)
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
    override fun editPicture() = Completable.fromAction {
        val bitmap = editIO.readPictureBitmap(originalPicture)
        mBitmap = bitmap
        changesModel.clear()
        getView()?.showBitmap(bitmap)
    }

    /**
     * applies all changes to the currently loaded bitmap and saves the
     * result to [originalPicture] (while keeping exif data)
     */
    override fun savePicture() = Completable.fromAction {

        if(changesModel.getSize() == 0) {
            return@fromAction
        }

        val allChanges = changesModel
        changesModel = ChangesModel(allChanges.getRectRadius())

        val edited = getView()?.commitChanges(allChanges) ?: return@fromAction
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
        mCanUndo.postValue(changesModel.getSize() > 0)
    }


}
