package com.zuliz.musicplayerdemo.music.service.playback

import android.support.v4.media.MediaMetadataCompat
import com.zuliz.musicplayerdemo.music.client.model.IMusicInfo

// 客户端通过连接 MediaBrowserCompat， 获取到 Session Token ，
// 通过 Session Token 拿到 MediaControllerCompat，
// 通过 MediaControllerCompat.transportControls 发送 action，
// 设置 MediaControllerCompat.Callback 拿到服务端的回调。
// 服务端创建 Service 并继承自 MediaBrowserServiceCompat，
// 创建 MediaSessionCompat 并获取到 Session Token，
// 设置 MediaSessionCompat.Callback 拿到客户端的 action，并执行对应操作
interface IPlayback {
    // 设置播放回调函数
    fun setPlaybackCallback(callback: PlaybackCallback)

    // 释放播放器
    fun release()

    // 开始和停止
    fun start()
    fun stop()

    // 用户操作
    fun playMusicList(mediaMetadataList: MutableList<MediaMetadataCompat>, playIndex: Int = 0)
    fun addMusicList(mediaMetadataList: MutableList<MediaMetadataCompat>)
    fun playFromMediaId(mediaId: String?)
    fun playOrPause()
    fun pause()
    fun seekTo(position: Long)
    fun previous()
    fun next()
    fun setSpeed(speed: Float)
    fun setRepeatMode(musicRepeatMode: MusicRepeatMode)

    // 从ExoPlayer获取信息
    fun isLoading(): Boolean
    fun isPrepared(): Boolean
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Long
    fun getBufferedPosition(): Long
    fun getBufferedPercentage(): Int
    fun getDuration(): Long
    fun getSpeed(): Float
}