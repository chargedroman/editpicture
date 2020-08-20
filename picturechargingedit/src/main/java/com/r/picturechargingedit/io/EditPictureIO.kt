package com.r.picturechargingedit.io

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.apache.sanselan.Sanselan
import org.apache.sanselan.formats.jpeg.JpegImageMetadata
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter
import org.apache.sanselan.formats.tiff.constants.TiffTagConstants.TIFF_TAG_ORIENTATION
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory
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
        return Glide.with(context)
            .asBitmap()
            .load(picture)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .submit(3000, 4000)
            .get()
    }


    fun savePicture(picture: Uri, bitmap: Bitmap) {
        val exif = readExif(picture)
        removeOrientation(exif)

        val inputStream = saveBitmapAndGetStream(bitmap)
        val outputStream = context.contentResolver.openOutputStream(picture)
        ExifRewriter().updateExifMetadataLossy(inputStream, outputStream, exif)
    }

    private fun removeOrientation(exif: TiffOutputSet?) {
        val directories = exif?.directories ?: return
        for(d in directories) {
            val dir = d as? TiffOutputDirectory
            dir?.removeField(TIFF_TAG_ORIENTATION)
        }
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
