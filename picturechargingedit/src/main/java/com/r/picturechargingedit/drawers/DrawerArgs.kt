package com.r.picturechargingedit.drawers

import com.r.picturechargingedit.mvp.EditPictureView

/**
 * abstraction for the view to pass to drawers
 *
 * Author: romanvysotsky
 * Created: 20.08.20
 */

class DrawerArgs(
    private val view: EditPictureView
) {

    fun getViewWidth(): Int {
        return view.width
    }

    fun getViewHeight(): Int {
        return view.height
    }

}
