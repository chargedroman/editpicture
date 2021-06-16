package com.r.imageedit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.mvp.EditPicturePresenter
import com.r.picturechargingedit.mvp.impl.EditPictureViewImpl
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var editView: EditPictureViewImpl
    lateinit var presenter: EditPicturePresenter
    lateinit var spinner: Spinner

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        editView = findViewById(R.id.view_edit)
        spinner = findViewById(R.id.spinner_mode)

        presenter = EditPicturePresenter.Factory(this)
            .create(getImageCacheUri())
        presenter.setThumbnailAspectRatio(9/16f)

        editView.setPresenter(presenter)

        setupSpinner()
        showPicture()
        enableDisableButtons()
    }

    override fun onResume() {
        super.onResume()
        Glide.get(this).setMemoryCategory(MemoryCategory.LOW)
    }

    override fun onPause() {
        super.onPause()
        Glide.get(this).setMemoryCategory(MemoryCategory.NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    fun onTakePhotoClicked(view: View) {
        takePictureAndOpenItInEditView()
    }

    fun onUndoLastActionClicked(view: View) {
        presenter.undoLastBlur()
    }

    fun onResetClicked(view: View) {
        presenter.resetChanges().sub(
            { println("CHAR: reset!") },
            { println("CHAR: $it") }
        )
    }

    fun onSaveResultClicked(view: View) {
        savePicture()
    }

    fun onCropClicked(view: View) {
        cropPicture()
    }

    fun onCropCircleClicked(view: View) {
        cropCirclePicture()
    }

    fun onThumbnailClicked(view: View) {
        thumbnailPicture()
    }

    fun onRotate90Clicked(view: View) {
        rotatePicture()
    }


    private fun showPicture() {
        presenter.editPicture().sub(
            { println("CHAR: edit!") },
            { println("CHAR: $it") }
        )
    }

    private fun rotatePicture() {
        presenter.rotatePictureBy90().sub(
            { println("CHAR: rotate!") },
            { println("CHAR: $it") }
        )
    }

    private fun savePicture() {
        presenter.savePicture().sub(
            { println("CHAR: saved") },
            { println("CHAR: $it")}
        )
    }

    private fun cropPicture() {
        presenter.cropPicture().sub(
            { println("CHAR: cropped") },
            { println("CHAR: $it")}
        )
    }

    private fun cropCirclePicture() {
        presenter.cropCirclePicture().sub(
            { println("CHAR: cropped circle") },
            { println("CHAR: $it")}
        )
    }

    private fun thumbnailPicture() {

        val dimen = presenter.createThumbnailDimensions()
        println("CHAR: dimen=$dimen")

        presenter.createThumbnail(getThumbnailImageCacheUri()).sub(
            { println("CHAR: thumbnailed") },
            { println("CHAR: $it")}
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            showPicture()
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


    private fun getThumbnailImageCacheUri(): Uri {
        val internalDir = application.filesDir
        val file = File(internalDir, "CapturedImageThumbnail.jpg")
        val authority = BuildConfig.APPLICATION_ID + ".fileprovider"
        return FileProvider.getUriForFile(application, authority, file)
    }

    private fun getImageCacheUri(): Uri {
        val internalDir = application.filesDir
        val file = File(internalDir, "CapturedImage.jpg")
        val authority = BuildConfig.APPLICATION_ID + ".fileprovider"
        return FileProvider.getUriForFile(application, authority, file)
    }


    private fun Completable.sub(success: () -> Unit, error: (Throwable) -> Unit) {
        val disposable = this.subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(success, error)
        disposables.add(disposable)
    }


    private fun setupSpinner() {
        val options = EditPictureMode.values().map { it.name }
        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options)
        spinner.onItemSelectedListener = this
    }


    private fun enableDisableButtons() {
        val undoButton: Button = findViewById(R.id.btn_undo)
        presenter.getCanUndoBlur().observe(this, Observer {
            undoButton.isEnabled = it
        })

        val resetButton: Button = findViewById(R.id.btn_reset)
        presenter.getCanResetChanges().observe(this, Observer {
            resetButton.isEnabled = it
        })
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val mode = EditPictureMode.values()[p2]
        presenter.setMode(mode)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) { }

}
