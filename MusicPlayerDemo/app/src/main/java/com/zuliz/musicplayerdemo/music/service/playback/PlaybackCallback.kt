package com.zuliz.musicplayerdemo.music.service.playback

import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.MediaItem

interface PlaybackCallback {
    fun onLoading(isLoading: Boolean)
    fun onPlayStateChanged(@PlaybackStateCompat.State state: Int)
    fun onMetadataChanged(mediaItem: MediaItem?)
    fun onPlaybackProgress(position: Long, duration: Long, buffering: Long)

    fun onError()
}