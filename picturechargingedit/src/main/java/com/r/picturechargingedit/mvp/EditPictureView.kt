package com.r.picturechargingedit.mvp

import android.graphics.Bitmap
import androidx.annotation.RestrictTo
import com.r.picturechargingedit.EditPictureMode
import com.r.picturechargingedit.arch.BaseView
import com.r.picturechargingedit.model.Changes
import com.r.picturechargingedit.model.Picture


/**
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

interface EditPictureView: BaseView {

    fun setPresenter(presenter: EditPicturePresenter)
    fun getPresenter(): EditPicturePresenter?


    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showMode(mode: EditPictureMode)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showPicture(pictureModel: Picture)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showChanges(changesModel: Changes)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun drawChanges(changesModel: Changes): Bitmap?

}
