package com.r.picturechargingedit.io

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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

    suspend fun loadBitmap(picture: Uri, width: Int, height: Int): Bitmap = withContext(Dispatchers.IO) {
        val target = Glide.with(context)
            .asBitmap()
            .load(picture)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .submit(height, width)
        return@withContext target.get()
    }

}
