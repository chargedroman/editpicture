package com.r.picturechargingedit.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
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

class EditPictureIOImpl(
    private val context: Context,
    private val downSampleSize: Int = SIZE_4_K
): EditPictureIO {

    companion object {
        const val SIZE_4_K = 4096
        const val QUALITY_MAX = 100
    }


    /**
     * rotates, downsamples, returns as bitmap
     * @return result bitmap
     */
    override fun readPictureBitmap(picture: Uri): Bitmap {
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
    override fun readPictureFile(picture: Uri): File {
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
    override fun downSample(original: Uri, saveLocation: Uri) {
        val downSampled = readPictureFile(original)
        val downSampledUri = Uri.fromFile(downSampled)

        val inputStream = context.contentResolver.openInputStream(downSampledUri)!!
        val outputStream = context.contentResolver.openOutputStream(saveLocation)!!

        inputStream.use {
            outputStream.use {
                inputStream.copyTo(outputStream)
            }
        }

        downSampled.delete()
    }


    /**
     *
     * @return exif of [picture] or an empty set
     */
    override fun readExif(picture: Uri, removeOrientationTag: Boolean): TiffOutputSet {
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
    override fun savePicture(saveLocation: Uri, bitmap: Bitmap, pictureExif: TiffOutputSet) {
        savePicture(saveLocation, bitmap, pictureExif, QUALITY_MAX)
    }

    /**
     * saves [bitmap] to [saveLocation] with [quality]
     */
    override fun savePicture(saveLocation: Uri, bitmap: Bitmap, quality: Int) {
        savePicture(saveLocation, bitmap, emptyExif(), quality)
    }

    /**
     * @return a bitmap which is the subarea of [bitmap] defined by [rect]
     */
    override fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap {
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
    }


    private fun savePicture(saveLocation: Uri, bitmap: Bitmap, pictureExif: TiffOutputSet, quality: Int) {

        val tmpFileName = UUID.randomUUID().toString()

        context.openFileOutput(tmpFileName, Context.MODE_PRIVATE).save(bitmap, quality)

        val inputStream = context.openFileInput(tmpFileName)
        inputStream.saveWithExifTo(saveLocation, pictureExif)

        File(context.filesDir, tmpFileName).delete()
    }

    private fun InputStream.saveWithExifTo(saveLocation: Uri, exif: TiffOutputSet) {
        val outputStream = context.contentResolver.openOutputStream(saveLocation)
        this.use { ExifRewriter().updateExifMetadataLossy(it, outputStream, exif) }
    }

    private fun OutputStream.save(bitmap: Bitmap, quality: Int) {
        this.use { bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it) }
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
