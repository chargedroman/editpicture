package com.r.picturechargingedit.mvp

import android.graphics.Bitmap
import com.r.picturechargingedit.arch.BaseView
import com.r.picturechargingedit.model.ChangesModel

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

interface EditPicture: BaseView {

    fun showBitmap(bitmap: Bitmap)
    fun showChanges(changesModel: ChangesModel)

    fun commitChanges(changesModel: ChangesModel): Bitmap?

}
