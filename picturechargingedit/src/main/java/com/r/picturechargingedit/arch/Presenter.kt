package com.r.picturechargingedit.arch

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

abstract class Presenter<T: BaseView>: BasePresenter<T> {

    private var view: T? = null

    override fun attach(view: T) {
        this.view = view
    }

    override fun detach() {
        this.view = null
    }

    override fun getView(): T? {
        return view
    }

}
