package com.zuliz.musicplayerdemo.widget

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

class MusicSeekBar : AppCompatSeekBar {
    private var mIsTracking = false

    /**
     * 属性动画
     */
    private var mProgressAnimator: ValueAnimator? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener?) {
        super.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                l?.onProgressChanged(seekBar, progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                l?.onStartTrackingTouch(seekBar)
                mIsTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                l?.onStopTrackingTouch(seekBar)
                mIsTracking = false
            }
        })
    }

    fun startProgressAnim(start: Int, end: Int, duration: Long) {
        stopProgressAnim()
        mProgressAnimator = ValueAnimator.ofInt(start, end).setDuration(duration)
        mProgressAnimator!!.interpolator = LinearInterpolator()
        mProgressAnimator!!.addUpdateListener { animation -> onProgressUpdate(animation) }
    }

    fun stopProgressAnim() {
        mProgressAnimator?.cancel()
        mProgressAnimator = null
    }

    fun onProgressUpdate(valueAnimator: ValueAnimator) {
        if (mIsTracking) {
            valueAnimator.cancel()
            return
        }
        val animatedIntValue = valueAnimator.animatedValue as Int
        progress = animatedIntValue
    }
}