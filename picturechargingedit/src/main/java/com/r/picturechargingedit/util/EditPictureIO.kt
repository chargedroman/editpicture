package com.r.picturechargingedit.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import com.r.picturechargingedit.model.Rotation
import org.apache.sanselan.formats.tiff.write.TiffOutputSet

/**
 *
 * Author: romanvysotsky
 * Created: 25.08.20
 */

interface EditPictureIO {

    companion object {

        const val SIZE_4_K = 4096
        const val QUALITY_MAX = 100

        fun create(context: Context): EditPictureIO {
            return EditPictureIOImpl(context)
        }

    }


    /**
     * rotates, downsamples, [picture] returns as bitmap
     * @return result bitmap
     */
    fun readPictureBitmap(
        picture: Uri,
        downSampleSize: Int = SIZE_4_K,
        rotation: Rotation = Rotation.None
    ): Bitmap


    /**
     * saves [bitmap] to [saveLocation] with [quality] and
     * overwrites [saveLocation]'s exif with [pictureExif]
     */
    fun savePicture(
        saveLocation: Uri,
        bitmap: Bitmap,
        pictureExif: TiffOutputSet = emptyExif(),
        quality: Int = QUALITY_MAX
    )

    /**
     * reads [original] into a file, rotated (from exif info) and down sampled,
     * then copies it to [saveLocation] including it's original exif
     */
    fun downSample(
        original: Uri,
        saveLocation: Uri,
        downSampleSize: Int = SIZE_4_K,
        quality: Int = QUALITY_MAX
    )

    /**
     * @return a bitmap which is the subarea of [bitmap] defined by [rect]
     */
    fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap

    /**
     *
     * @return exif of [picture] or an empty set
     */
    fun readExif(picture: Uri, removeOrientationTag: Boolean = true): TiffOutputSet

    /**
     *
     * @return the uri of one temp location where it is save to do i/o with
     */
    fun getBackupLocation(): Uri

    /**
     * @return an empty exif set
     */
    fun emptyExif(): TiffOutputSet

}