package com.r.picturechargingedit.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.r.picturechargingedit.model.PixelAverage
import com.r.picturechargingedit.model.Rotation
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPictureIOImpl(private val context: Context): EditPictureIO {


    /**
     * @return the [PixelAverage] for the given [picture].
     */
    override fun getPixelAverage(picture: Uri): PixelAverage {
        val smallBitmap = readPictureBitmap(picture, 64)
        val width = smallBitmap.width
        val height = smallBitmap.height
        val pixels = IntArray(width * height)
        smallBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        smallBitmap.recycle()
        return PixelAverage(pixels)
    }


    /**
     * rotates, downsamples, returns as bitmap
     * @return result bitmap
     */
    override fun readPictureBitmap(picture: Uri, downSampleSize: Int, rotation: Rotation): Bitmap {
        return Glide.with(context)
            .asBitmap()
            .load(picture)
            .transform(Rotate(rotation.angle))
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
            .submit(downSampleSize, downSampleSize)
            .get()
    }


    /**
     * reads [original] into a bitmap, rotated (from exif info) and down sampled with [downSampleSize],
     * then saves it to [saveLocation] without any EXIF data
     */
    override fun downSample(
        original: Uri,
        saveLocation: Uri,
        downSampleSize: Int,
        quality: Int
    ) {
        val bitmap = readPictureBitmap(original, downSampleSize)
        savePicture(
            saveLocation = saveLocation,
            bitmap = bitmap,
            quality = quality
        )
        bitmap.recycle()
    }


    /**
     * saves [bitmap] to [saveLocation] as a JPEG with [quality], without any EXIF data.
     * The JPEG is built in memory first, so a failed compression leaves [saveLocation] untouched.
     */
    override fun savePicture(
        saveLocation: Uri,
        bitmap: Bitmap,
        quality: Int
    ) {
        val jpegBytes = ByteArrayOutputStream().use { out ->
            val compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            if (!compressed) throw IOException("Bitmap.compress failed for $saveLocation")
            out.toByteArray()
        }

        val output = context.contentResolver.openOutputStream(saveLocation)
            ?: throw IOException("Could not open output stream for $saveLocation")

        output.use { it.write(jpegBytes) }
    }


    /**
     * @return a bitmap which is the subarea of [bitmap] defined by [rect]
     */
    override fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap {
        rect.bottom = rect.bottom.coerceAtMost(bitmap.height)
        rect.right = rect.right.coerceAtMost(bitmap.width)
        rect.top = rect.top.coerceAtLeast(0)
        rect.left = rect.left.coerceAtLeast(0)
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
    }


    /**
     *
     * @return the uri of one temp location where it is save to do i/o with
     */
    override fun getBackupLocation(): Uri {
        return Uri.fromFile(File(context.filesDir, "EditPictureIOBackupLocation"))
    }


}
