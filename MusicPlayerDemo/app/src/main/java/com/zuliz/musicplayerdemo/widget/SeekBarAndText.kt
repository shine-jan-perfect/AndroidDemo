package com.zuliz.musicplayerdemo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatSeekBar
import android.text.TextPaint


class SeekBarAndText : AppCompatSeekBar {
    // 画笔
    private var mProgressTextPaint: Paint? = null

    // 进度文字位置信息
    private val mProgressTextRect: Rect = Rect()

    // 进度文字
    private var mProgressText: String = ""

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initProgressTextPaint()
        initThumbOffset()
    }

    private fun initProgressTextPaint() {
        mProgressTextPaint = TextPaint() //初始化画笔
        mProgressTextPaint?.isAntiAlias = true //消除锯齿
        mProgressTextPaint?.isDither = true
        mProgressTextPaint?.color = Color.BLACK //画笔颜色
        mProgressTextPaint?.textSize = sp2px(22f).toFloat() //字体大小
    }

    private fun initThumbOffset() {
        thumbOffset = 0
    }

    override fun onDraw(canvas: Canvas?) {
        // 滑块上要画的文字
        val progressText = getProgressText()
        // 滑块上的文字
        mProgressTextPaint?.getTextBounds(
            progressText,
            0,
            progressText.length,
            mProgressTextRect
        )
        // 文字宽度
        val progressTextWidth = mProgressTextRect.width()
        // 滑块宽度
        val thumbWidth = progressTextWidth + getThumbPadding()

        // 进度百分比
        val progressRatio = progress.toFloat() / max

        // 滑块能够滑动的总距离
        val thumbOffsetTotalDistance = width - thumbWidth
        // 滑块滑动距离
        val thumbOffset = thumbOffsetTotalDistance * progressRatio

        // 文字当前位置
        val progressTextX = thumbOffset + getThumbPadding() / 2f
        val progressTextY = height / 2f + mProgressTextRect.height() / 2f

        // 滑块当前位置
        val thumbX = thumbOffset.toInt()
        val thumbY = this.thumb.bounds.top
        // 移动滑块位置
        if (progressRatio > 0f && thumbOffset < thumbOffsetTotalDistance) {
            this.thumb.bounds.offsetTo(
                thumbX,
                thumbY
            )
        }
        // super中绘制滑块位置
        super.onDraw(canvas)

        if (progressRatio > 0f && thumbOffset < thumbOffsetTotalDistance) {
            // 画滑块上的文字
            mProgressTextPaint?.let {
                canvas!!.drawText(
                    progressText,
                    progressTextX,
                    progressTextY,
                    it
                )
            }
            // 移动文字
            mProgressTextRect.offsetTo(
                progressTextX.toInt(),
                progressTextY.toInt()
            )
        } else {
            // 画滑块上的文字
            mProgressTextPaint?.let {
                canvas!!.drawText(
                    progressText,
                    ((thumbWidth - mProgressTextRect.width()) / 2f),
                    progressTextY,
                    it
                )
            }
        }
    }

    /**
     * dp转px
     *
     * @param dp dp值
     * @return px值
     */
    private fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }

    /**
     * sp转px
     *
     * @param sp sp值
     * @return px值
     */
    private fun sp2px(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            resources.displayMetrics
        )
    }

    fun getProgressText(): String {
        return mProgressText
    }

    fun setProgressText(progressText: String) {
        mProgressText = progressText

        setThumbBounds()
    }

    private fun setThumbBounds() {
        mProgressTextPaint?.getTextBounds(
            mProgressText,
            0,
            mProgressText.length,
            mProgressTextRect
        )
        val progressTextWidth = mProgressTextRect.width()

        // change thumb bounds in order to change thumb size
        this.thumb.bounds.left = this.thumbOffset
        this.thumb.bounds.right = (progressTextWidth + this.thumbOffset + getThumbPadding()).toInt()
        this.thumb.bounds = this.thumb.bounds
    }

    /**
     * 滑块左右总padding，左右各边padding=滑块左右总padding/2
     */
    private fun getThumbPadding(): Float {
        return dp2px(15f) * 2f
    }
}