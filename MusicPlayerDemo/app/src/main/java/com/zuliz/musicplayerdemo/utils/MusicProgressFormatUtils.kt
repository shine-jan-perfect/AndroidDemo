package com.zuliz.musicplayerdemo.utils

import java.text.SimpleDateFormat
import java.util.*

object MusicProgressFormatUtils {
    private fun formatMillisecond2Hms(millisecond: Long?): String {
        if (millisecond == null) {
            return "00:00"
        }
        //这里想要只保留分秒可以写成"mm:ss"
        val formatter = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
        //这里很重要，如果不设置时区的话，输出结果就会是几点钟，而不是毫秒值对应的时分秒数量了。
        formatter.timeZone = TimeZone.getTimeZone("GMT+00:00")
        return formatter.format(millisecond)
    }

    fun formatMaxAndProgress2ProgressText(max: Int?, progress: Int?): String {
        val maxText = formatMillisecond2Hms(max?.toLong())
        val progressText = formatMillisecond2Hms(progress?.toLong())
        return "$progressText/$maxText"
    }
}