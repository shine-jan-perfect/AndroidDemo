package com.zuliz.circleprogressview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.graphics.*
import android.text.TextPaint
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat

class CircleProgressView : View {
    /**
     * 进度条颜色
     */
    private var mProgressColor = Color.BLACK

    /**
     * 进度条宽度
     */
    private var mProgressWidth = CommonUtil.dp2px(context, 10)

    /**
     * 当前进度
     */
    private var mProgress = 50f

    /**
     * 最大进度
     */
    private var mMaxProgress = 100f

    /**
     * 进度条画笔
     */
    private var mProgressPaint: Paint? = null

    /**
     * 进度条背景颜色
     */
    private var mProgressBackgroundColor = Color.GRAY

    /**
     * 进度条背景画笔
     */
    private var mProgressBackgroundPaint: Paint? = null

    /**
     * 进度条上面的小圆颜色
     */
    private var mSmallCircleColor = Color.WHITE

    /**
     * 进度条上的小圆半径
     */
    private var mSmallCircleRadius = CommonUtil.dp2px(context, 5)

    /**
     * 进度上面的小圆实心画笔
     */
    private var mSmallCirclePaint: Paint? = null

    /**
     * 当前进度字体大小
     */
    private var mProgressTextSize = CommonUtil.sp2px(context, 30)

    /**
     * 当前进度字体颜色
     */
    private var mProgressTextColor = Color.BLACK

    /**
     * 当前进度文字
     */
    private var mProgressText = "0"

    /**
     * 当前进度文字画笔
     */
    private var mProgressTextPaint: TextPaint? = null

    /**
     * title字体大小
     */
    private var mProgressTitleTextSize = CommonUtil.sp2px(context, 16)

    /**
     * title字体颜色
     */
    private var mProgressTitleTextColor = Color.GRAY

    /**
     * title文字
     */
    private var mProgressTitleText = "今日"

    /**
     * title文字画笔
     */
    private var mProgressTitleTextPaint: TextPaint? = null

    /**
     * subtitle字体大小
     */
    private var mProgressSubtitleTextSize = CommonUtil.sp2px(context, 16)

    /**
     * subtitle字体颜色
     */
    private var mProgressSubtitleTextColor = Color.GRAY

    /**
     * subtitle文字
     */
    private var mProgressSubtitleText = "步"

    /**
     * subtitle文字画笔
     */
    private var mProgressSubtitleTextPaint: TextPaint? = null

    /**
     * 进度条起始角度
     */
    private var mStartAngle = -90

    /**
     * 进度条终止角度
     */
    private var mEndAngle = 270

    /**
     * view 宽度
     */
    private var mViewWidth = CommonUtil.dp2px(context, 20)

    /**
     * View 绘制区域
     */
    private var mRectF = RectF()

    /**
     * 是否显示进度条动画
     */
    private var mShowAnimation = false

    /**
     * 进度条动画持续时间
     */
    private var mAnimationDuration = 1_000L

    /**
     * 值动画
     */
    private var mValueAnimator: ValueAnimator? = null

    /**
     * 进度变化监听
     */
    private var mOnProgressChangedListener: OnProgressChangedListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initPaint()

        initAttrs(attrs, defStyleAttr)

        initPaintAfterInitAttrs()
    }

    private fun initPaint() {
        mProgressPaint = Paint()
        mProgressPaint?.isAntiAlias = true
        mProgressPaint?.isDither = true
        mProgressPaint?.style = Paint.Style.STROKE
        mProgressPaint?.strokeCap = Paint.Cap.ROUND

        mSmallCirclePaint = Paint()
        mSmallCirclePaint?.isAntiAlias = true
        mSmallCirclePaint?.isDither = true
        mSmallCirclePaint?.style = Paint.Style.FILL

        mProgressBackgroundPaint = Paint()
        mProgressBackgroundPaint?.isAntiAlias = true
        mProgressBackgroundPaint?.isDither = true
        mProgressBackgroundPaint?.style = Paint.Style.STROKE
        mProgressBackgroundPaint?.strokeCap = Paint.Cap.ROUND

        mProgressTextPaint = TextPaint()
        mProgressTextPaint?.isAntiAlias = true
        mProgressTextPaint?.isDither = true
        mProgressTextPaint?.style = Paint.Style.FILL

        mProgressTitleTextPaint = TextPaint()
        mProgressTitleTextPaint?.isAntiAlias = true
        mProgressTitleTextPaint?.isDither = true
        mProgressTitleTextPaint?.style = Paint.Style.FILL

        mProgressSubtitleTextPaint = TextPaint()
        mProgressSubtitleTextPaint?.isAntiAlias = true
        mProgressSubtitleTextPaint?.isDither = true
        mProgressSubtitleTextPaint?.style = Paint.Style.FILL
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView, defStyleAttr, 0)

        mProgressWidth = typedArray.getDimension(
            R.styleable.CircleProgressView_progressWidth,
            mProgressWidth.toFloat()
        ).toInt()

        mProgressColor = typedArray.getColor(
            R.styleable.CircleProgressView_progressColor, ContextCompat.getColor(
                context, android.R.color.black
            )
        )

        mSmallCircleColor = typedArray.getColor(
            R.styleable.CircleProgressView_smallCircleColor,
            ContextCompat.getColor(
                context, android.R.color.white
            )
        )

        mSmallCircleRadius = typedArray.getDimension(
            R.styleable.CircleProgressView_smallCircleRadius,
            mSmallCircleRadius.toFloat()
        ).toInt()

        mProgressBackgroundColor =
            typedArray.getColor(
                R.styleable.CircleProgressView_progressBackgroundColor, ContextCompat.getColor(
                    context, android.R.color.darker_gray
                )
            )

        // progress text
        mProgressTextColor = typedArray.getColor(
            R.styleable.CircleProgressView_progressTextColor, ContextCompat.getColor(
                context, android.R.color.black
            )
        )

        mProgressTextSize = typedArray.getDimension(
            R.styleable.CircleProgressView_progressTextSize,
            mProgressTextSize.toFloat()
        ).toInt()

        mProgressText =
            typedArray.getString(R.styleable.CircleProgressView_progressText) ?: mProgressText

        // progress title text
        mProgressTitleTextColor = typedArray.getColor(
            R.styleable.CircleProgressView_progressTitleTextColor, ContextCompat.getColor(
                context, android.R.color.darker_gray
            )
        )

        mProgressTitleTextSize = typedArray.getDimension(
            R.styleable.CircleProgressView_progressTitleTextSize,
            mProgressTitleTextSize.toFloat()
        ).toInt()

        mProgressTitleText =
            typedArray.getString(R.styleable.CircleProgressView_progressTitleText)
                ?: mProgressTitleText

        // progress subtitle text
        mProgressSubtitleTextColor = typedArray.getColor(
            R.styleable.CircleProgressView_progressSubtitleTextColor, ContextCompat.getColor(
                context, android.R.color.darker_gray
            )
        )

        mProgressSubtitleTextSize = typedArray.getDimension(
            R.styleable.CircleProgressView_progressSubtitleTextSize,
            mProgressSubtitleTextSize.toFloat()
        ).toInt()

        mProgressSubtitleText =
            typedArray.getString(R.styleable.CircleProgressView_progressSubtitleText)
                ?: mProgressSubtitleText

        mStartAngle = typedArray.getInt(R.styleable.CircleProgressView_startAngle, mStartAngle)

        mEndAngle = typedArray.getInt(R.styleable.CircleProgressView_startAngle, mEndAngle)

        mShowAnimation =
            typedArray.getBoolean(R.styleable.CircleProgressView_showAnimation, mShowAnimation)

        mAnimationDuration =
            typedArray.getInt(
                R.styleable.CircleProgressView_animationDuration,
                mAnimationDuration.toInt()
            ).toLong()

        typedArray.recycle()
    }

    private fun initPaintAfterInitAttrs() {
        mProgressPaint?.strokeWidth = mProgressWidth.toFloat()
        mProgressPaint?.color = mProgressColor

        mSmallCirclePaint?.color = mSmallCircleColor

        mProgressBackgroundPaint?.strokeWidth = mProgressWidth.toFloat()
        mProgressBackgroundPaint?.color = mProgressBackgroundColor

        mProgressTextPaint?.strokeWidth = 0f
        mProgressTextPaint?.textSize = mProgressTextSize.toFloat()
        mProgressTextPaint?.color = mProgressTextColor

        mProgressTitleTextPaint?.strokeWidth = 0f
        mProgressTitleTextPaint?.textSize = mProgressTitleTextSize.toFloat()
        mProgressTitleTextPaint?.color = mProgressTitleTextColor

        mProgressSubtitleTextPaint?.strokeWidth = 0f
        mProgressSubtitleTextPaint?.textSize = mProgressSubtitleTextSize.toFloat()
        mProgressSubtitleTextPaint?.color = mProgressSubtitleTextColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width: Int = measureWidth(widthMeasureSpec)
        val height: Int = measureHeight(heightMeasureSpec)

        mViewWidth = Math.min(width, height)

        setMeasuredDimension(mViewWidth, mViewWidth)
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val width: Int
        val size = MeasureSpec.getSize(widthMeasureSpec)
        val mode = MeasureSpec.getMode(widthMeasureSpec)

        width = when (mode) {
            MeasureSpec.EXACTLY -> {
                if (size < mProgressWidth) mProgressWidth else size
            }
            MeasureSpec.AT_MOST -> {
                mViewWidth * 2
            }
            else -> {
                CommonUtil.getScreenWidthInPx(context)
            }
        }

        return width
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val height: Int
        val size = MeasureSpec.getSize(heightMeasureSpec)
        val mode = MeasureSpec.getMode(heightMeasureSpec)

        height = when (mode) {
            MeasureSpec.EXACTLY -> {
                if (size < mProgressWidth) mProgressWidth else size
            }
            MeasureSpec.AT_MOST -> {
                mViewWidth * 2
            }
            else -> {
                CommonUtil.getScreenHeightInPx(context)
            }
        }

        return height
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        mRectF.left = ((mProgressWidth / 2).toFloat())
        mRectF.top = ((mProgressWidth / 2).toFloat())
        mRectF.right = ((mViewWidth - mProgressWidth / 2).toFloat())
        mRectF.bottom = ((mViewWidth - mProgressWidth / 2).toFloat())

        // 绘制进度条背景
        mProgressBackgroundPaint?.let {
            canvas?.drawCircle(
                (mViewWidth / 2).toFloat(),
                (mViewWidth / 2).toFloat(),
                ((mViewWidth / 2 - mProgressWidth / 2).toFloat()),
                it
            )
        }

        // 绘制进度条
        mProgressPaint?.let {
            canvas?.drawArc(
                mRectF,
                mStartAngle.toFloat(), mProgress * 360 / mMaxProgress, false, it
            )
        }

        // 画布中心点
        val centerX = width / 2
        val centerY = height / 2
        // 进度条半径
        val radius = (mViewWidth / 2 - mProgressWidth / 2).toFloat()

        // 绘制进度上的小圆
        val currentAngle = mProgress * 360 / mMaxProgress + mStartAngle
        mSmallCirclePaint?.let {
            val radian = Math.PI * currentAngle / 180 //Math.abs：绝对值 ，Math.PI：表示π ， 弧度 = 度*π / 180
            val smallCircleX =
                (Math.cos(radian) * radius + centerX).toFloat()
            val smallCircleY =
                (Math.sin(radian) * radius + centerY).toFloat()
            canvas?.drawCircle(smallCircleX, smallCircleY, mSmallCircleRadius.toFloat(), it)
        }

        // 绘制当前进度文字
        val progressTextWidth = mProgressTextPaint?.measureText(mProgressText) ?: 0f // 文字宽度
        val progressTextHeight =
            mProgressTextPaint?.ascent() ?: 0f + (mProgressTextPaint?.descent() ?: 0f) // 文字高度
        mProgressTextPaint?.let {
            canvas?.drawText(
                mProgressText,
                centerX - progressTextWidth / 2,
                centerY - progressTextHeight / 2,
                it
            )
        }

        // 绘制 title 文字
        val titleTextWidth = mProgressTitleTextPaint?.measureText(mProgressTitleText) ?: 0f // 文字宽度
        val titleTextHeight =
            mProgressTitleTextPaint?.ascent() ?: 0f + (mProgressTitleTextPaint?.descent()
                ?: 0f) // 文字高度
        mProgressTitleTextPaint?.let {
            canvas?.drawText(
                mProgressTitleText,
                centerX - titleTextWidth / 2,
                centerY - titleTextHeight / 2 + progressTextHeight,
                it
            )
        }

        // 绘制 subtitle 文字
        val subtitleTextWidth =
            mProgressSubtitleTextPaint?.measureText(mProgressSubtitleText) ?: 0f // 文字宽度
        val subtitleTextHeight =
            mProgressSubtitleTextPaint?.ascent() ?: 0f + (mProgressSubtitleTextPaint?.descent()
                ?: 0f) // 文字高度
        mProgressSubtitleTextPaint?.let {
            canvas?.drawText(
                mProgressSubtitleText,
                centerX - subtitleTextWidth / 2,
                centerY - subtitleTextHeight / 2 - progressTextHeight,
                it
            )
        }
    }

    /**
     * 设置进度进度条宽度
     */
    fun setProgressWidth(progressWidth: Float) {
        mProgressWidth = progressWidth.toInt()
        mProgressPaint?.strokeWidth = progressWidth
        mProgressBackgroundPaint?.strokeWidth = progressWidth
    }

    /**
     * 设置进度条颜色
     */
    fun setProgressColor(progressColor: Int) {
        mProgressColor = progressColor
        mProgressPaint?.color = mProgressColor
    }

    /**
     * 设置背景颜色
     */
    fun setProgressBackgroundColor(backgroundColor: Int) {
        mProgressBackgroundColor = backgroundColor
        mProgressBackgroundPaint?.color = mProgressBackgroundColor
        invalidate()
    }

    fun setMaxProgress(maxProgress: Float) {
        mMaxProgress = maxProgress
    }

    /**
     * 设置进度
     * @param progress      进度
     * @param showAnimation 是否展示动画
     */
    fun setProgress(progress: Float, showAnimation: Boolean) {
        mShowAnimation = showAnimation

        if (mValueAnimator != null && mValueAnimator!!.isRunning) {
            mValueAnimator!!.cancel()
        }

        if (mShowAnimation) {
            mValueAnimator = ValueAnimator.ofFloat(progress)
            mValueAnimator?.duration = mAnimationDuration
            mValueAnimator?.interpolator = LinearInterpolator()
            mValueAnimator?.addUpdateListener {
                mProgress = it.animatedValue as Float
                mOnProgressChangedListener?.onProgressChanged(mProgress * mMaxProgress / mMaxProgress)
                invalidate()
            }
            mValueAnimator?.start()
        } else {
            mProgress = progress
            invalidate()
        }
    }

    /**
     * 设置动画持续时间
     */
    fun setAnimationDuration(animationDuration: Long) {
        mAnimationDuration = animationDuration
    }

    /**
     * 设置进度起始角度
     */
    fun setStartAngle(startAngle: Int) {
        mStartAngle = startAngle
    }

    /**
     * 设置切割圆结束角度
     */
    fun setEndAngle(endAngle: Int) {
        mEndAngle = endAngle
    }

    /**
     * 设置进度监听
     */
    fun setOnProgressChangedListener(listener: OnProgressChangedListener) {
        mOnProgressChangedListener = listener
    }

    interface OnProgressChangedListener {
        fun onProgressChanged(currentProgress: Float)
    }
}