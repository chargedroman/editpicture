package com.r.picturechargingedit.arch

/**
 *
 * Author: romanvysotsky
 * Created: 20.08.20
 */

interface BasePresenter<T: BaseView> {

    fun attach(view: T)
    fun detach()
    fun getView(): T?

}
