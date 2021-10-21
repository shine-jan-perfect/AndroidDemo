package com.zuliz.musicplayerdemo.utils

import java.util.concurrent.TimeUnit

object MusicProgressFormatUtils {
    /**
     * @return mm:ss
     */
    private fun formatMillisecond2Hms(millisecond: Long?): String {
        if (millisecond == null) {
            return "00:00"
        }

        /*val minutes = millisecond / 1000 / 60
        val seconds = millisecond % 1000

        val minutesHmsText = if (minutes < 10) {
            "0$minutes"
        } else {
            minutes
        }
        val secondsHmsText = if (seconds < 10) {
            "0$seconds"
        } else {
            seconds
        }*/
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisecond) % TimeUnit.MINUTES.toSeconds(1)
        val minutesHmsText = if (minutes < 10) {
            "0$minutes"
        } else {
            minutes
        }
        val secondsHmsText = if (seconds < 10) {
            "0$seconds"
        } else {
            seconds
        }
        /*return String.format("%d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millisecond),
            TimeUnit.MILLISECONDS.toSeconds(millisecond) % TimeUnit.MINUTES.toSeconds(1))*/
        return "$minutesHmsText:$secondsHmsText"
    }

    /**
     * @return mm:ss/mm:ss
     */
    fun formatMaxAndProgress2ProgressText(max: Int?, progress: Int?): String {
        val maxHmsText = formatMillisecond2Hms(max?.toLong())
        val progressHmsText = formatMillisecond2Hms(progress?.toLong())
        return "$progressHmsText/$maxHmsText"
    }
}