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
import java.io.OutputStream
import java.util.*


/**
 *
 * Author: romanvysotsky
 * Created: 18.08.20
 */

class EditPictureIO(private val context: Context, private val downSampleSize: Int = SIZE_4_K) {

    companion object {
        const val SIZE_4_K = 4096
    }


    /**
     * rotates, downsamples, returns as bitmap
     * @return result bitmap
     */
    fun readPictureBitmap(picture: Uri): Bitmap {
        return Glide.with(context)
            .asBitmap()
            .load(picture)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
            .submit(downSampleSize, downSampleSize)
            .get()
    }

    /**
     * rotates, downsamples [picture] and saves it to a cached file
     * @return result file location
     */
    fun readPictureFile(picture: Uri): File {
        return Glide.with(context)
            .downloadOnly()
            .load(picture)
            .skipMemoryCache(true)
            .downsample(DownsampleStrategy.CENTER_INSIDE)
            .submit(downSampleSize, downSampleSize)
            .get()
    }


    /**
     * reads [original] into a file, rotated (from exif info) and down sampled with [downSampleSize],
     * then copies it to [saveLocation] including it's original exif
     */
    fun downSample(original: Uri, saveLocation: Uri) {
        val originalExif = readExif(original)
        val file = readPictureFile(original)
        val downSampledUri = Uri.fromFile(file)
        val inputStream = context.contentResolver.openInputStream(downSampledUri)
        inputStream!!.saveWithExifTo(saveLocation, originalExif)
        file.delete()
    }


    /**
     *
     * @return exif of [picture] or an empty set
     */
    fun readExif(picture: Uri, removeOrientationTag: Boolean = true): TiffOutputSet {
        val exif = context.contentResolver.openInputStream(picture).use {
            val metadata = Sanselan.getMetadata(it, "") as? JpegImageMetadata
            metadata?.exif?.outputSet ?: emptyExif()
        }

        if(removeOrientationTag) {
            removeOrientationTag(exif)
        }

        return exif
    }

    /**
     * saves [bitmap] to [saveLocation] and overwrites [saveLocation]'s exif with [pictureExif]
     */
    fun savePicture(saveLocation: Uri, bitmap: Bitmap, pictureExif: TiffOutputSet = emptyExif()) {

        val tmpFileName = UUID.randomUUID().toString()

        context.openFileOutput(tmpFileName, Context.MODE_PRIVATE).save(bitmap)

        val inputStream = context.openFileInput(tmpFileName)
        inputStream.saveWithExifTo(saveLocation, pictureExif)

        File(context.filesDir, tmpFileName).delete()
    }


    private fun InputStream.saveWithExifTo(saveLocation: Uri, exif: TiffOutputSet) {
        val outputStream = context.contentResolver.openOutputStream(saveLocation)
        this.use { ExifRewriter().updateExifMetadataLossy(it, outputStream, exif) }
    }

    private fun OutputStream.save(bitmap: Bitmap) {
        this.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
    }


    private fun emptyExif(): TiffOutputSet {
        val set = TiffOutputSet()
        val dir = TiffOutputDirectory(TiffDirectoryConstants.DIRECTORY_TYPE_ROOT)
        set.addDirectory(dir)
        return set
    }

    private fun removeOrientationTag(exif: TiffOutputSet?) {
        val directories = exif?.directories ?: return
        for (d in directories) {
            val dir = d as? TiffOutputDirectory
            dir?.removeField(TIFF_TAG_ORIENTATION)
        }
    }

}
