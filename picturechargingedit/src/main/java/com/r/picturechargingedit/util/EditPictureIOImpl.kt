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
import org.apache.sanselan.ImageReadException
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
     * then copies it to [saveLocation] including it's original exif without the orientation tag
     */
    override fun downSample(
        original: Uri,
        saveLocation: Uri,
        downSampleSize: Int,
        quality: Int
    ) {
        val pictureExif = readExif(original, true)
        val bitmap = readPictureBitmap(original, downSampleSize)
        savePicture(
            saveLocation = saveLocation,
            bitmap = bitmap,
            pictureExif = pictureExif,
            quality = quality
        )
        bitmap.recycle()
    }


    /**
     *
     * @return exif of [picture] or an empty set
     */
    override fun readExif(picture: Uri, removeOrientationTag: Boolean): TiffOutputSet {
        val exif = context.contentResolver.openInputStream(picture).use {
            val metadata = readMetaDataOrNull(it)
            metadata?.exif?.outputSet ?: emptyExif()
        }

        if(removeOrientationTag) {
            removeOrientationTag(exif)
        }

        return exif
    }


    /**
     * saves [bitmap] to [saveLocation] with [quality] and
     * overwrites [saveLocation]'s exif with [pictureExif]
     */
    override fun savePicture(
        saveLocation: Uri,
        bitmap: Bitmap,
        pictureExif: TiffOutputSet,
        quality: Int
    ) {

        val tmpFileName = UUID.randomUUID().toString()

        context.openFileOutput(tmpFileName, Context.MODE_PRIVATE).save(bitmap, quality)

        val inputStream = context.openFileInput(tmpFileName)
        inputStream.saveWithExifTo(saveLocation, pictureExif)

        File(context.filesDir, tmpFileName).delete()
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


    /**
     * @return an empty exif set
     */
    override fun emptyExif(): TiffOutputSet {
        val set = TiffOutputSet()
        val dir = TiffOutputDirectory(TiffDirectoryConstants.DIRECTORY_TYPE_ROOT)
        set.addDirectory(dir)
        return set
    }


    private fun InputStream.saveWithExifTo(saveLocation: Uri, exif: TiffOutputSet) {
        val outputStream = context.contentResolver.openOutputStream(saveLocation)
        this.use { ExifRewriter().updateExifMetadataLossy(it, outputStream, exif) }
    }

    private fun OutputStream.save(bitmap: Bitmap, quality: Int) {
        this.use { bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it) }
    }


    private fun removeOrientationTag(exif: TiffOutputSet?) {
        val directories = exif?.directories ?: return
        for (d in directories) {
            val dir = d as? TiffOutputDirectory
            dir?.removeField(TIFF_TAG_ORIENTATION)
        }
    }


    private fun readMetaDataOrNull(inputStream: InputStream?): JpegImageMetadata? {
        return try {
            Sanselan.getMetadata(inputStream, "") as? JpegImageMetadata
        } catch (e: ImageReadException) {
            null
        }
    }

}
