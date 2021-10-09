package com.zuliz.lib_circle_progress_view

import android.content.Context
import android.util.DisplayMetrics

object CommonUtils {
    /**
     * dp转成px
     */
    fun dp2px(context: Context, dp: Int): Int {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        return (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    /**
     * sp转成px
     */
    fun sp2px(context: Context, sp: Int): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (sp * fontScale + 0.5f).toInt()
    }

    fun getScreenWidthInPx(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeightInPx(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }
}