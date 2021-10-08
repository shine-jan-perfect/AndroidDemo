package com.zuliz.musicplayerdemo.music.service

import android.support.v4.media.session.PlaybackStateCompat

interface PlaybackServiceCallback {
    fun onPlayStateChanged(playbackState: PlaybackStateCompat)
    fun onPlaybackProgress(position: Long, duration: Long, buffering: Long)
    fun onError()
}