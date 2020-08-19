package com.r.picturechargingedit.io

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import org.apache.sanselan.Sanselan
import org.apache.sanselan.formats.jpeg.JpegImageMetadata
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter
import org.apache.sanselan.formats.tiff.write.TiffOutputSet
import java.io.InputStream


/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPictureIO(private val context: Context) {

    companion object {
        const val TEMP_FILE_NAME = "tmpBufferedImage"
    }


    fun readPictureBitmap(picture: Uri): Bitmap {
        return BitmapFactory.decodeStream(context.contentResolver.openInputStream(picture))
    }

    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun savePicture(picture: Uri, bitmap: Bitmap) {
        val exif = readExif(picture)
        val inputStream = saveBitmapAndGetStream(bitmap)
        val outputStream = context.contentResolver.openOutputStream(picture)
        ExifRewriter().updateExifMetadataLossy(inputStream, outputStream, exif)
    }


    private fun saveBitmapAndGetStream(bitmap: Bitmap): InputStream? {
        val output = context.openFileOutput(TEMP_FILE_NAME, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
        return context.openFileInput(TEMP_FILE_NAME)
    }

    private fun readExif(picture: Uri): TiffOutputSet? {
        val inputStream = context.contentResolver.openInputStream(picture)
        val metadata = Sanselan.getMetadata(inputStream, "") as? JpegImageMetadata
        return metadata?.exif?.outputSet
    }

}
