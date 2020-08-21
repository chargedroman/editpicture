package com.r.picturechargingedit.util

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.math.round

/**
 *
 * Author: romanvysotsky
 * Created: 20.08.20
 */

class TransformBitmapMaxSize(private val maxSize: Int) : BitmapTransformation() {

    companion object {
        private const val ID = "com.r.picturechargingedit.io.TransformBitmapMaxSize"
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {

        if (toTransform.width <= maxSize && toTransform.height <= maxSize) {
            return toTransform
        }

        return if(toTransform.width >= toTransform.height) {
            val aspectRatio = toTransform.width / toTransform.height.toDouble()
            val width = maxSize
            val height = round(width / aspectRatio).toInt()
            Bitmap.createScaledBitmap(toTransform, width, height,true)
        } else {
            val aspectRatio = toTransform.height / toTransform.width.toDouble()
            val height = maxSize
            val width = round(height / aspectRatio).toInt()
            Bitmap.createScaledBitmap(toTransform, width, height,true)
        }
    }


    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val bytes = ID.toByteArray(Charset.forName("UTF-8"))
        messageDigest.update(bytes)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TransformBitmapMaxSize) return false
        if (maxSize != other.maxSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = maxSize
        result = 31 * result + ID.hashCode()
        return result
    }

}
