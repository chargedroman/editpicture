package com.r.picturechargingedit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPictureIO(private val context: Context) {

    suspend fun loadBitmap(picture: Uri): Bitmap = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(picture)!!.buffered().use {
            return@use BitmapFactory.decodeStream(it)
        }
    }

}
