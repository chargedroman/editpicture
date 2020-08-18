package com.r.picturechargingedit

import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.Job

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

interface EditView {

    fun showBitmap(bitmap: Bitmap)

}

interface EditPresenter {

    fun onPictureSelected(picture: Uri): Job

}
