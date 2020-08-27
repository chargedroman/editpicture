package com.r.picturechargingedit.mvp.impl

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.Presenter
import com.r.picturechargingedit.model.changes.Pixelation
import com.r.picturechargingedit.model.crop.Crop
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.mvp.EditPicturePresenter
import com.r.picturechargingedit.mvp.EditPictureView
import com.r.picturechargingedit.util.EditPictureIO
import io.reactivex.Completable

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPicturePresenterImpl(
    private val originalPicture: Uri,
    private val editIO: EditPictureIO,
    private val pictureModel: Picture,
    private val changesModel: Pixelation,
    private val cropModel: Crop
) : Presenter<EditPictureView>(),
    EditPicturePresenter {

    companion object {
        const val INITIAL_RECT_RADIUS_FACTOR = 1f
        private const val THOUSAND = 1000f
        private const val RELATIVE_RECT_RADIUS_FACTOR = 1.6f
    }


    private val mCanUndo: MutableLiveData<Boolean> = MutableLiveData(false)
    private val mMode: MutableLiveData<EditPictureMode> = MutableLiveData(EditPictureMode.NONE)
    private var mRelativeRectRadius = INITIAL_RECT_RADIUS_FACTOR
    private val lock = Object()

    override fun getRectRadius(): Float = mRelativeRectRadius

    override fun getCanUndo() = mCanUndo
    override fun getMode() = mMode


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
        getView()?.showMode(mode)
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
        changesModel.clear()
        pictureModel.setBitmap(bitmap)
        mRelativeRectRadius = getRelativePixelatedRectRadius(bitmap)
        getView()?.showPicture(pictureModel)
    }

    /**
     * applies all changes to the currently loaded bitmap and saves the
     * result to [originalPicture] (while keeping exif data)
     */
    override fun savePicture() = completable {

        if(changesModel.getSize() == 0) {
            return@completable
        }

        val edited = getView()?.drawChanges(pictureModel, changesModel) ?: return@completable
        val originalExif = editIO.readExif(originalPicture)
        editIO.savePicture(originalPicture, edited, originalExif)

        changesModel.clear()
        pictureModel.setBitmap(edited)

        getView()?.showPicture(pictureModel)
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }


    override fun startRecordingDraw(x: Float, y: Float, radius: Float) {
        if(mMode.value == EditPictureMode.NONE) return

        changesModel.startRecordingDraw(x, y, radius)
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }

    override fun continueRecordingDraw(x: Float, y: Float, radius: Float) {
        if(mMode.value == EditPictureMode.NONE) return
        if(mMode.value == EditPictureMode.PIXELATE_VIA_CLICK) return

        changesModel.continueRecordingDraw(x, y, radius)
        getView()?.showChanges(changesModel)
        updateCanUndo()
    }


    override fun attach(view: EditPictureView) {
        super.attach(view)
        view.showPicture(pictureModel)
        view.showChanges(changesModel)

        val mode = mMode.value ?: return
        view.showMode(mode)
    }


    private fun updateCanUndo() {
        mCanUndo.postValue(changesModel.getSize() > 0)
    }


    /**
     * utility to synchronize access to io operations on [originalPicture]
     */
    private fun completable(block: () -> Unit) = Completable.fromAction {
        synchronized(lock) {
            block()
        }
    }


    private fun getRelativePixelatedRectRadius(bitmap: Bitmap): Float {
        val width = bitmap.width/THOUSAND
        val height = bitmap.height/THOUSAND
        return width * height * RELATIVE_RECT_RADIUS_FACTOR
    }


}
