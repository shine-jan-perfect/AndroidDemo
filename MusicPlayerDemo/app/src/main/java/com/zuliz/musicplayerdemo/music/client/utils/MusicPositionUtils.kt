package com.zuliz.musicplayerdemo.music.client.utils

import kotlin.math.floor

object MusicPositionUtils {
    //时间转换成分：秒的格式
    fun getCurrentPosition(position: Long): String {
        val totalSeconds = floor(position * 1000.0).toInt()
        val minutes = totalSeconds / 60
        val remainingSeconds = totalSeconds - minutes * 60
        return if (position < 0) {
            "--:--"
        } else
            String.format("%d:%02d", minutes, remainingSeconds)
    }

    fun getCurrentPositionFromSeekbar(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val remainingSeconds = totalSeconds - minutes * 60
        return if (totalSeconds < 0) {
            "--:--"
        } else
            String.format("%d:%02d", minutes, remainingSeconds)
    }

    fun formatSecond2MillisSecond(position: Long): Int {
        return floor(position * 1000.0).toInt()
    }

    fun formatMillisSecond2Second(position: Long): Int {
        return floor(position * 1.0 / 1000.0).toInt()
    }
}