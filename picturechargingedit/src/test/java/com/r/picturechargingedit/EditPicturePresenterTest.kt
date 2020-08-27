package com.r.picturechargingedit

import android.graphics.Bitmap
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import com.r.picturechargingedit.model.Changes
import com.r.picturechargingedit.model.Picture
import com.r.picturechargingedit.mvp.EditPictureView
import com.r.picturechargingedit.mvp.impl.EditPicturePresenterImpl
import com.r.picturechargingedit.util.EditPictureIO
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.apache.sanselan.formats.tiff.write.TiffOutputSet
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EditPicturePresenterTest {

    @get:Rule val rule = InstantTaskExecutorRule()


    val viewMock: EditPictureView = mock()
    val pictureUriMock: Uri = mock()
    val editPictureMock: EditPictureIO = mock()
    val changesMock: Changes = mock()
    val pictueModelMock: Picture = mock()

    val changesModelFactory: (Float) -> Changes = { changesMock }


    fun initPresenter(): EditPicturePresenterImpl {
        whenever(changesMock.getPictureModel()).thenReturn(pictueModelMock)
        return EditPicturePresenterImpl(
            pictureUriMock,
            editPictureMock,
            changesModelFactory
        )
            .also { it.attach(viewMock) }
    }

    @Before
    fun initWithMainScheduler() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }


    @Test
    fun undoLastAction_UndoAll_DoThat() {

        val presenter = initPresenter()

        presenter.undoLastAction(undoAll = true)

        verify(changesMock, times(1)).clear()
        verify(changesMock, times(0)).removeLast()
        verify(viewMock, times(2)).showChanges(changesMock)
    }

    @Test
    fun undoLastAction_NotUndoAll_RemoveLast() {

        val presenter = initPresenter()

        presenter.undoLastAction(undoAll = false)

        verify(changesMock, times(0)).clear()
        verify(changesMock, times(1)).removeLast()
        verify(viewMock, times(2)).showChanges(changesMock)
    }


    @Test
    fun setRectRadius_DoThat() {

        val presenter = initPresenter()
        val radius = 42f

        presenter.setRectRadiusFactor(radius)

        verify(changesMock, times(1)).setRectRadiusFactor(radius)
        verify(viewMock, times(2)).showChanges(changesMock)
    }


    @Test
    fun setMode_ShowMode_ClearChangesSet_DoThat() {

        val presenter = initPresenter()
        val mode = EditPictureMode.PIXELATE_VIA_DRAG

        presenter.setMode(mode, true)

        assertEquals(mode, presenter.getMode().value)
        verify(viewMock, times(1)).showMode(mode)
        verify(changesMock, times(1)).clear()
        verify(viewMock, times(2)).showChanges(changesMock)
    }

    @Test
    fun setMode_ShowMode_ClearChangesNotSet_DontClear() {

        val presenter = initPresenter()
        val mode = EditPictureMode.PIXELATE_VIA_DRAG

        presenter.setMode(mode, false)

        assertEquals(presenter.getMode().value, mode)
        verify(viewMock, times(1)).showMode(mode)
        verify(changesMock, times(0)).clear()
        verify(viewMock, times(1)).showChanges(changesMock)
    }


    @Test
    fun editPicture_ReadBitmapFromIO_UpdatePictureModel_ShowPictureModel() {

        val presenter = initPresenter()

        val bitmap: Bitmap = mock()
        whenever(editPictureMock.readPictureBitmap(pictureUriMock)).thenReturn(bitmap)

        presenter.editPicture().blockingAwait()

        verify(changesMock, times(1)).clear()
        verify(pictueModelMock, times(1)).setBitmap(bitmap)
        //check for 2, because attach(view) also calls it once
        verify(viewMock, times(2)).showPicture(pictueModelMock)
    }


    @Test
    fun savePicture_NoChanges_Return() {

        val presenter = initPresenter()
        whenever(changesMock.getSize()).thenReturn(0)

        presenter.savePicture().blockingAwait()

        verify(viewMock, times(0)).drawChanges(any())
    }

    @Test
    fun savePicture_SaveEditedBitmap_WithExifOfOriginal() {

        val editedBitmap: Bitmap = mock()
        val exif = TiffOutputSet()

        val presenter = initPresenter()
        whenever(changesMock.getSize()).thenReturn(1)
        whenever(viewMock.drawChanges(changesMock)).thenReturn(editedBitmap)
        whenever(editPictureMock.readExif(pictureUriMock, true)).thenReturn(exif)

        presenter.savePicture().blockingAwait()

        verify(viewMock, times(1)).drawChanges(changesMock)
        verify(editPictureMock, times(1)).savePicture(pictureUriMock, editedBitmap, exif)
        verify(viewMock, times(2)).showChanges(changesMock)
        verify(viewMock, times(2)).showPicture(pictueModelMock)
    }

    @Test
    fun attach_ShowPicture_GetViewNonNull() {

        val presenter = initPresenter()

        assertEquals(viewMock, presenter.getView())
        verify(viewMock, times(1)).showPicture(changesMock.getPictureModel())
    }

    @Test
    fun attach_ShowPicture_detach_GetViewNull() {

        val presenter = initPresenter()
        presenter.detach()

        assertEquals(null, presenter.getView())
        verify(viewMock, times(1)).showPicture(changesMock.getPictureModel())
    }


    @Test
    fun startRecordingDraw_ModeNone_DoNothing() {

        val x = 0f
        val y = 0f
        val r = 0f

        val presenter = initPresenter()
        presenter.startRecordingDraw(x, y, r)

        verify(changesMock, times(0)).startRecordingDraw(any(), any(), any())
    }

    @Test
    fun startRecordingDraw_ModeDrag_StartRecording() {

        val mode = EditPictureMode.PIXELATE_VIA_DRAG

        val x = 0f
        val y = 0f
        val r = 0f

        whenever(changesMock.getSize()).thenReturn(1)

        val presenter = initPresenter()
        presenter.setMode(mode)
        presenter.startRecordingDraw(x, y, r)

        assertEquals(true, presenter.getCanUndo().value)
        verify(changesMock, times(1)).startRecordingDraw(x, y, r)
        verify(viewMock, times(2)).showChanges(changesMock)
    }

    @Test
    fun continueRecordingDraw_ModeNoneOrViaClick_DoNothing() {

        val modeNone = EditPictureMode.NONE
        val modeClick = EditPictureMode.PIXELATE_VIA_CLICK

        val x = 0f
        val y = 0f
        val r = 0f

        val presenter = initPresenter()


        presenter.setMode(modeNone)
        presenter.continueRecordingDraw(x, y, r)
        verify(changesMock, times(0)).continueRecordingDraw(any(), any(), any())

        presenter.setMode(modeClick)
        presenter.continueRecordingDraw(x, y, r)
        verify(changesMock, times(0)).continueRecordingDraw(any(), any(), any())

    }

    @Test
    fun continueRecordingDraw_ModeDrag_ContinueRecording() {

        val mode = EditPictureMode.PIXELATE_VIA_DRAG

        val x = 0f
        val y = 0f
        val r = 0f

        val presenter = initPresenter()

        presenter.setMode(mode)
        presenter.continueRecordingDraw(x, y, r)
        verify(changesMock, times(1)).continueRecordingDraw(x, y, r)
        verify(viewMock, times(2)).showChanges(changesMock)
    }


}
