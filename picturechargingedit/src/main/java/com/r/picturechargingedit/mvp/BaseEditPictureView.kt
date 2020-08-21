package com.r.picturechargingedit.mvp

import android.graphics.Bitmap
import androidx.annotation.RestrictTo
import com.r.picturechargingedit.arch.BaseView
import com.r.picturechargingedit.model.ChangesModel
import com.r.picturechargingedit.model.PictureModel

/**
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

interface BaseEditPictureView: BaseView {

    fun setPresenter(presenter: BaseEditPicturePresenter)
    fun getPresenter(): BaseEditPicturePresenter?

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showPicture(pictureModel: PictureModel)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showChanges(changesModel: ChangesModel)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun drawChanges(changesModel: ChangesModel): Bitmap?

}
