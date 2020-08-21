package com.r.picturechargingedit.mvp

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
class EditPictureView : View, BaseEditPictureView {

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

    private var presenter: BaseEditPicturePresenter? = null


    fun setEditPicturePresenter(presenter: BaseEditPicturePresenter) {
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


    override fun showBitmap(bitmap: Bitmap) {
        drawerArgs.bitmap = bitmap
        post(this::invalidate)
    }

    override fun showChanges(changesModel: ChangesModel) {
        drawerPixelatedPath.showChanges(changesModel.getRectModels(drawerArgs.radius))
        post(this::invalidate)
    }

    override fun commitChanges(changesModel: ChangesModel): Bitmap? {
        val bitmap = drawerArgs.bitmap ?: return null
        val canvas = Canvas(bitmap)
        val matrix = drawerArgs.createInvertedMatrix()
        val radius = matrix.mapRadius(drawerArgs.radius)
        changesModel.mapAllCoordinates(matrix)

        val changes = changesModel.getRectModels(radius)
        drawerPixelatedPath.drawChangesOnCanvas(changes, canvas)

        return bitmap
    }


}
