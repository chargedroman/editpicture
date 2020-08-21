package com.r.picturechargingedit.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import org.apache.sanselan.Sanselan
import org.apache.sanselan.formats.jpeg.JpegImageMetadata
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter
import org.apache.sanselan.formats.tiff.constants.TiffDirectoryConstants
import org.apache.sanselan.formats.tiff.constants.TiffTagConstants.TIFF_TAG_ORIENTATION
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory
import org.apache.sanselan.formats.tiff.write.TiffOutputSet
import java.io.File
import java.io.InputStream
import java.util.*


/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPictureIO(private val context: Context) {

    companion object {
        const val MAX_SIZE = 4096
    }


    fun readPictureBitmap(picture: Uri): Bitmap {
        return Glide.with(context)
            .asBitmap()
            .load(picture)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
            .submit(MAX_SIZE, MAX_SIZE)
            .get()
    }


    fun savePicture(picture: Uri, bitmap: Bitmap) {
        val tmpName = UUID.randomUUID().toString()
        val exif = readExif(picture) ?: emptyExif()
        removeOrientation(exif)

        val inputStream = saveBitmapAndGetStream(tmpName, bitmap)
        val outputStream = context.contentResolver.openOutputStream(picture)

        inputStream?.use { ExifRewriter().updateExifMetadataLossy(it, outputStream, exif) }

        File(context.filesDir, tmpName).delete()
    }


    private fun emptyExif(): TiffOutputSet {
        val set = TiffOutputSet()
        val dir = TiffOutputDirectory(TiffDirectoryConstants.DIRECTORY_TYPE_ROOT)
        set.addDirectory(dir)
        return set
    }

    private fun removeOrientation(exif: TiffOutputSet?) {
        val directories = exif?.directories ?: return
        for(d in directories) {
            val dir = d as? TiffOutputDirectory
            dir?.removeField(TIFF_TAG_ORIENTATION)
        }
    }

    private fun saveBitmapAndGetStream(tmpName: String, bitmap: Bitmap): InputStream? {
        context.openFileOutput(tmpName, Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return context.openFileInput(tmpName)
    }

    private fun readExif(picture: Uri): TiffOutputSet? = context.contentResolver.openInputStream(picture).use {
        val metadata = Sanselan.getMetadata(it, "") as? JpegImageMetadata
        return metadata?.exif?.outputSet
    }

}
