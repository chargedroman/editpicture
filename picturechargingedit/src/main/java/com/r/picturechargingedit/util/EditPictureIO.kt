package com.r.picturechargingedit.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import org.apache.sanselan.formats.tiff.write.TiffOutputSet
import java.io.File

/**
 *
 * Author: romanvysotsky
 * Created: 25.08.20
 */

interface EditPictureIO {


    companion object {

        fun create(context: Context): EditPictureIO {
            return EditPictureIOImpl(context)
        }

        fun create(context: Context, downSampleSize: Int): EditPictureIO {
            return EditPictureIOImpl(context, downSampleSize)
        }

    }

    /**
     * rotates, downsamples, [picture] returns as bitmap
     * @return result bitmap
     */
    fun readPictureBitmap(picture: Uri): Bitmap

    /**
     * rotates, downsamples [picture] and saves it to a cached file
     * @return result file location
     */
    fun readPictureFile(picture: Uri): File

    /**
     * saves [bitmap] to [saveLocation] and overwrites [saveLocation]'s exif with [pictureExif]
     */
    fun savePicture(saveLocation: Uri, bitmap: Bitmap, pictureExif: TiffOutputSet)

    /**
     * saves [bitmap] to [saveLocation] with [quality]
     */
    fun savePicture(saveLocation: Uri, bitmap: Bitmap, quality: Int)

    /**
     * @return a bitmap which is the subarea of [bitmap] defined by [rect]
     */
    fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap

    /**
     * reads [original] into a file, rotated (from exif info) and down sampled with [downSampleSize],
     * then copies it to [saveLocation] including it's original exif
     */
    fun downSample(original: Uri, saveLocation: Uri)

    /**
     *
     * @return exif of [picture] or an empty set
     */
    fun readExif(picture: Uri, removeOrientationTag: Boolean = true): TiffOutputSet

}