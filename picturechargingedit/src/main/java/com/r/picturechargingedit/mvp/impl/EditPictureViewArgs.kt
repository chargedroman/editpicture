package com.r.picturechargingedit.mvp.impl

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import com.r.picturechargingedit.R

/**
 * Wrapper for all custom view args of [EditPictureViewImpl]
 *
 * Author: romanvysotsky
 * Created: 07.09.20
 */

class EditPictureViewArgs(context: Context, attrs: AttributeSet?) {

    val accentColor: Int
    val backgroundColor: Int
    val standardBackgroundColor: Int
    val standardForegroundColor: Int
    val cropCaption: String
    val thumbCaption: String


    init {
        val attributes = typedArray(context, attrs)
        accentColor = accent(context, attributes)
        backgroundColor = background(context, attributes)
        cropCaption = cropCaption(attributes)
        thumbCaption = thumbCaption(attributes)
        standardBackgroundColor = context.getColor(R.color.colorStandardBackgroundDraw)
        standardForegroundColor = context.getColor(R.color.colorStandardForegroundDraw)
        attributes?.recycle()
    }


    private fun accent(context: Context, typedArray: TypedArray?): Int {
        val default = context.getColor(R.color.colorAccentDraw)
        return typedArray?.getColor(R.styleable.EditPictureViewImpl_accentColor, default) ?: default
    }

    private fun background(context: Context, typedArray: TypedArray?): Int {
        val default = context.getColor(R.color.colorBackgroundDraw)
        return typedArray?.getColor(R.styleable.EditPictureViewImpl_backgroundColor, default) ?: default
    }

    private fun cropCaption(typedArray: TypedArray?): String {
        return typedArray?.getString(R.styleable.EditPictureViewImpl_cropCaption) ?: ""
    }

    private fun thumbCaption(typedArray: TypedArray?): String {
        return typedArray?.getString(R.styleable.EditPictureViewImpl_thumbCaption) ?: ""
    }


    private fun typedArray(context: Context, attrs: AttributeSet?): TypedArray? {
        return if(attrs == null)
            null
        else
            context.obtainStyledAttributes(attrs, R.styleable.EditPictureViewImpl)
    }

}
