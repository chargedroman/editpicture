package com.r.picturechargingedit.mvp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import com.r.picturechargingedit.drawers.DrawerArgs
import com.r.picturechargingedit.drawers.DrawerPicture
import com.r.picturechargingedit.drawers.DrawerPixelatedPath
import com.r.picturechargingedit.model.ChangesModel
import com.r.picturechargingedit.model.PictureModel
import com.r.picturechargingedit.scale.ScalingView

/**
 *
 * Author: romanvysotsky
 * Created: 19.08.20
 */

@SuppressLint("ClickableViewAccessibility")
class EditPictureView : ScalingView, BaseEditPictureView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val drawerArgs = DrawerArgs(this)
    private val drawerPicture = DrawerPicture(drawerArgs)
    private val drawerPixelatedPath = DrawerPixelatedPath(drawerArgs)

    private var presenter: BaseEditPicturePresenter? = null


    override fun setPresenter(presenter: BaseEditPicturePresenter) {
        this.presenter = presenter
        presenter.attach(this)
    }

    override fun getPresenter(): BaseEditPicturePresenter? {
        return presenter
    }


    override fun onDetachedFromWindow() {
        presenter?.detach()
        super.onDetachedFromWindow()
    }


    override fun onDrawScaled(canvas: Canvas) {
        drawerPicture.onDraw(canvas)
        drawerPixelatedPath.onDraw(canvas)
    }


    override fun onTouchEventScaled(action: Int, x: Float, y: Float) {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                presenter?.startRecordingDraw(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                presenter?.continueRecordingDraw(x, y)
            }
        }
    }


    override fun showPicture(pictureModel: PictureModel) {
        drawerPicture.showChanges(pictureModel)
        post(this::invalidate)
    }

    override fun showChanges(changesModel: ChangesModel) {
        drawerPixelatedPath.showChanges(changesModel)
        post(this::invalidate)
    }

    override fun drawChanges(changesModel: ChangesModel): Bitmap? {

        val canvas = changesModel.pictureModel.createCanvas() ?: return null
        changesModel.invertAllCoordinates()
        drawerPixelatedPath.drawChangesOnCanvas(changesModel, canvas)

        return changesModel.pictureModel.bitmap
    }

}
