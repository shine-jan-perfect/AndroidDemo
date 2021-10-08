package com.zuliz.musicplayerdemo.music.client

interface ProgressChangedCallback {
    fun onDurationChanged(duration: Int)
    fun onProgressChanged(currentProgress: Int)
    fun onBufferedProgressChanged(bufferedProgress: Int)
}