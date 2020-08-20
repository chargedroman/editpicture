package com.r.picturechargingedit.v2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.r.picturechargingedit.drawers.DrawerArgs
import com.r.picturechargingedit.drawers.DrawerBitmap
import com.r.picturechargingedit.drawers.DrawerPixelatedPath
import com.r.picturechargingedit.model.ChangesModel

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

@SuppressLint("ClickableViewAccessibility")
class EditPictureView : View, EditPicture {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val drawerArgs = DrawerArgs(this)
    private val drawerBitmap = DrawerBitmap(drawerArgs)
    private val drawerPixelatedPath = DrawerPixelatedPath(drawerArgs)

    private var presenter: EditPicturePresenter? = null


    fun setEditPicturePresenter(presenter: EditPicturePresenter) {
        this.presenter = presenter
        presenter.attach(this)
    }


    override fun onDetachedFromWindow() {
        presenter?.detach()
        super.onDetachedFromWindow()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawerBitmap.onDraw(canvas)
        drawerPixelatedPath.onDraw(canvas)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event == null) {
            return super.onTouchEvent(event)
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                presenter?.startRecordingDraw(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                presenter?.continueRecordingDraw(event.x, event.y)
            }
        }

        return true
    }


    override fun showBitmap(bitmap: Bitmap) = handler.run {
        drawerArgs.bitmap = bitmap
        invalidate()
    }

    override fun showChanges(changesModel: ChangesModel) = handler.run {
        drawerPixelatedPath.showPaths(changesModel)
        invalidate()
    }

    override fun commitChanges(changesModel: ChangesModel) = handler.run {
        val bitmap = drawerArgs.bitmap ?: return@run null
        val canvas = Canvas(bitmap)
        val matrix = drawerArgs.createInvertedMatrix()
        changesModel.mapAllCoordinates(matrix)
        drawerPixelatedPath.drawChangesOnCanvas(changesModel, canvas)
        return@run bitmap
    }

}
