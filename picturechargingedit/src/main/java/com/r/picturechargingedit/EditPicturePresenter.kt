package com.r.picturechargingedit

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPicturePresenter(
    private val view: EditView,
    private val editIO: EditPictureIO
): EditPresenter {

    private val mainScope = CoroutineScope(Job() + Dispatchers.Main)


    override fun onPictureSelected(picture: Uri) = mainScope.launch {
        val bitmap = editIO.loadBitmap(picture)
        view.showBitmap(bitmap)
    }



    class Factory(private val context: Context) {
        fun create(view: EditView): EditPicturePresenter {
            val io = EditPictureIO(context)
            return EditPicturePresenter(view, io)
        }
    }


}
