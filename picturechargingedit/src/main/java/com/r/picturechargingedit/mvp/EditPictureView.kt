package com.r.picturechargingedit.mvp

import android.graphics.Canvas
import androidx.annotation.RestrictTo
import com.r.picturechargingedit.arch.BaseView
import com.r.picturechargingedit.model.crop.Crop
import com.r.picturechargingedit.model.picture.Picture
import com.r.picturechargingedit.model.pixelation.Pixelation
import com.r.picturechargingedit.model.scale.Scale


/**
 *
 * Author: romanvysotsky
 * Created: 21.08.20
 */

interface EditPictureView: BaseView {

    fun setPresenter(presenter: EditPicturePresenter)
    fun getPresenter(): EditPicturePresenter?


    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun notifyChanged()
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showCrop(cropModel: Crop)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showScale(scaleModel: Scale)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showPicture(pictureModel: Picture)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun showPixelation(pixelationModel: Pixelation)
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun drawPixelation(pixelationModel: Pixelation, canvas: Canvas)

}
