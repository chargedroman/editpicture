package com.r.imageedit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.r.picturechargingedit.EditPictureView
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var editView: EditPictureView
    lateinit var btnTakePhoto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        editView = findViewById(R.id.view_edit)
        btnTakePhoto = findViewById(R.id.btn_take_photo)

        editView.onPictureSelected(getImageCacheUri())
    }


    fun onTakePhotoClicked(view: View) {
        takePictureAndOpenItInEditView()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            editView.onPictureSelected(getImageCacheUri())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePictureAndOpenItInEditView()
        }
    }

    private fun takePictureAndOpenItInEditView() {

        if(!hasCameraPermission()) {
            requestCameraPermission()
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageCacheUri())
        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        startActivityForResult(intent, 1234)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 12345)
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun getImageCacheUri(): Uri {
        val internalDir = application.filesDir
        val file = File(internalDir, "CapturedImage")
        val authority = BuildConfig.APPLICATION_ID + ".fileprovider"
        return FileProvider.getUriForFile(application, authority, file)
    }

}
