package com.r.picturechargingedit.arch

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

abstract class BasePresenter<T: BaseView> {

    private var view: T? = null

    fun attach(view: T) {
        this.view = view
    }

    fun detach() {
        this.view = null
    }

    fun getView(): T? {
        return view
    }

}
