package com.r.picturechargingedit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.r.picturechargingedit.io.EditPictureIO
import com.r.picturechargingedit.model.EditPictureResultArgs
import com.r.picturechargingedit.model.PathModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPictureViewModel(
    private val editIO: EditPictureIO
): ViewModel() {


    val resizedBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val fullSizeBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val pathModels: MutableLiveData<List<PathModel>> = MutableLiveData()

    private val allPathModels = LinkedList<PathModel>()
    private var currentPathModel: PathModel? = null
    private var finishEditing: EditPictureResultArgs? = null


    private val mainScope = CoroutineScope(Job() + Dispatchers.Main)


    fun onPictureSelected(picture: Uri, width: Int, height: Int) = mainScope.launch {
        val bitmap = editIO.loadBitmap(picture, width, height)
        resizedBitmap.value = bitmap
    }



    fun startRecordingDraw(x: Float, y: Float) {
        val bitmap = resizedBitmap.value ?: return
        val pathModel = PathModel(bitmap.width, bitmap.height)
        pathModel.add(x, y)

        allPathModels.add(pathModel)
        currentPathModel = pathModel
        pathModels.value = allPathModels
    }

    fun continueRecordingDraw(x: Float, y: Float) {
        currentPathModel?.add(x, y)
        pathModels.value = allPathModels
    }

    fun undoLastAction() {
        if(allPathModels.size > 0) {
            allPathModels.removeLast()
            pathModels.value = allPathModels
        }
    }

    fun onSaveResultClicked(args: EditPictureResultArgs) = mainScope.launch {
        val bitmap = editIO.loadFullSizeBitmap(args.picture)
        finishEditing = args
        fullSizeBitmap.value = bitmap
    }

    fun onUserEditingWasCopiedTo(bitmap: Bitmap) = mainScope.launch {
        val args = finishEditing ?: return@launch
        editIO.savePicture(args.picture, bitmap)
    }


    class Factory(private val context: Context) {
        fun create(): EditPictureViewModel {
            val io = EditPictureIO(context)
            return EditPictureViewModel(io)
        }
    }


}
