package com.r.picturechargingedit

import android.net.Uri
import com.nhaarman.mockitokotlin2.mock
import com.r.picturechargingedit.model.Changes
import com.r.picturechargingedit.mvp.EditPictureView
import com.r.picturechargingedit.mvp.impl.EditPicturePresenterImpl
import com.r.picturechargingedit.util.EditPictureIO
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EditPicturePresenterTest {

    val viewMock: EditPictureView = mock()

    val pictureUriMock: Uri = mock()
    val editIOMock: EditPictureIO = mock()
    val changesMock: Changes = mock()

    val changesModelFactory: (Float) -> Changes = { changesMock }

    fun initPresenter(): EditPicturePresenterImpl {
        return EditPicturePresenterImpl(
            pictureUriMock,
            editIOMock,
            changesModelFactory
        )
            .also { it.attach(viewMock) }
    }

    @Test
    fun setMode_ShowsMode() {

        val presenter = initPresenter()


    }

}
