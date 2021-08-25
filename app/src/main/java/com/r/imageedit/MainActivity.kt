package com.r.imageedit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.*
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
import com.r.picturechargingedit.util.EditPictureIO
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        val REQUEST_CODE_GET = 123
        val REQUEST_CODE_TAKE = 1234
        val REQUEST_CODE_CAMERA_PERMISSION = 12345
    }


    lateinit var editView: EditPictureViewImpl
    lateinit var presenter: EditPicturePresenter
    lateinit var spinner: Spinner
    lateinit var progress: ProgressBar

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        editView = findViewById(R.id.view_edit)
        spinner = findViewById(R.id.spinner_mode)
        progress = findViewById(R.id.progress)

        presenter = EditPicturePresenter.Factory(applicationContext).create(getImageCacheUri())
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

    fun onGetPhotoClicked(view: View) {
        getPictureAndOpenItInEditView()
    }

    fun onUndoLastActionClicked(view: View) {
        presenter.undoLastBlur()
    }

    fun onResetClicked(view: View) {
        presenter.resetChanges().sub(
            { show("CHAR: reset!") },
            { show("CHAR: $it") }
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

    fun onIsBrightClicked(view: View) {
        checkIsBright()
    }


    private fun checkIsBright() {
        presenter.getPixelAverage().sub(
            { show("CHAR: done! $it") },
            { show("CHAR: $it") }
        )
    }


    private fun showPicture() {
        presenter.editPicture().sub(
            { show("CHAR: edit!") },
            { show("CHAR: $it") }
        )
    }

    private fun showPicture(externalPicture: Uri) {
        val io = EditPictureIO.create(applicationContext)

        Completable.fromAction {
            io.downSample(externalPicture, getImageCacheUri())
        }.andThen(presenter.editPicture()).sub(
            { show("CHAR: edit external!") },
            { show("CHAR: $it") }
        )
    }


    private fun rotatePicture() {
        presenter.rotatePictureBy90().sub(
            { show("CHAR: rotate!") },
            { show("CHAR: $it") }
        )
    }

    private fun savePicture() {
        presenter.savePicture().sub(
            { show("CHAR: saved") },
            { show("CHAR: $it")}
        )
    }

    private fun cropPicture() {
        presenter.cropPicture().sub(
            { show("CHAR: cropped") },
            { show("CHAR: $it")}
        )
    }

    private fun cropCirclePicture() {
        presenter.cropCirclePicture().sub(
            { show("CHAR: cropped circle") },
            { show("CHAR: $it")}
        )
    }

    private fun thumbnailPicture() {

        val dimen = presenter.createThumbnailDimensions()
        show("CHAR: dimen=$dimen")

        presenter.createThumbnail(getThumbnailImageCacheUri()).sub(
            { show("CHAR: thumbnailed") },
            { show("CHAR: $it")}
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_TAKE && resultCode == Activity.RESULT_OK) {
            showPicture()
        }

        if(requestCode == REQUEST_CODE_GET && resultCode == Activity.RESULT_OK) {
            val image = data?.data ?: return
            showPicture(image)
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
        startActivityForResult(intent, REQUEST_CODE_TAKE)
    }

    private fun getPictureAndOpenItInEditView() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GET)
    }


    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
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
        val disposable = this
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .doOnSubscribe { show(loading = true) }
            .doFinally { show(loading = false) }
            .subscribe(success, error)
        disposables.add(disposable)
    }

    private fun <T> Single<T>.sub(success: (T) -> Unit, error: (Throwable) -> Unit) {
        val disposable = this
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .doOnSubscribe { show(loading = true) }
            .doFinally { show(loading = false) }
            .subscribe(success, error)
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
    
    
    private fun show(message: String) = executeOnMainThread {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun show(loading: Boolean) = executeOnMainThread {
        if(loading) {
            progress.visibility = View.VISIBLE
        } else {
            progress.visibility = View.GONE
        }
    }

    private inline fun executeOnMainThread(crossinline block: () -> Unit) {
        val handler = Handler(mainLooper)
        handler.post { block() }
    }

}
