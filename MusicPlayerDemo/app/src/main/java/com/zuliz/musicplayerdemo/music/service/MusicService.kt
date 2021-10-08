package com.zuliz.musicplayerdemo.music.service

import android.app.Service
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import android.content.Intent
import android.os.RemoteException
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.session.MediaButtonReceiver
import com.zuliz.musicplayerdemo.music.service.notification.MusicNotificationManager
import java.lang.IllegalStateException
import com.zuliz.musicplayerdemo.music.service.playback.MusicPlayback

class MusicService : MediaBrowserServiceCompat() {
    companion object {
        const val TAG = "MediaPlaybackService"

        private const val MEDIA_ID_EMPTY_ROOT = "__MEDIA_ID_EMPTY_ROOT__"
        private const val MEDIA_ID_ROOT = "__MEDIA_ID_ROOT__"
    }

    private var mMusicPlaybackManager: MusicPlaybackManager? = null
    private var mMusicNotificationManager: MusicNotificationManager? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate()")
        super.onCreate()

        initMusicPlayback()
        initMusicNotification()
        initReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        if (intent != null) {
            // Try to handle the intent as a media button event wrapped by MediaButtonReceiver
            if (mMusicPlaybackManager != null && mMusicPlaybackManager!!.mMediaSession != null) {
                MediaButtonReceiver.handleIntent(mMusicPlaybackManager!!.mMediaSession, intent)
            }
        }
        return Service.START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        super.onDestroy()
        // 释放音乐播放器
        mMusicPlaybackManager?.release()
        // TODO 释放通知栏
        mMusicNotificationManager?.stopNotification()
        // TODO 注销广播
    }

    // ############# MediaBrowserServiceCompat #############
    // onGetRoot 校验客户端包名并返回给 onLoadChildren BrowserRoot
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        if (clientPackageName != packageName) {
            return BrowserRoot(MEDIA_ID_EMPTY_ROOT, null)
        }
        return BrowserRoot(MEDIA_ID_ROOT, null)
    }

    // onLoadChildren 判断 onGetRoot 返回的 BrowserRoot 中的 rootId，设置返回给客户端的 result
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (MEDIA_ID_EMPTY_ROOT == parentId) {
            result.sendResult(mutableListOf())
        }
    }
    // ############# MediaBrowserServiceCompat #############

    // ############# 初始化 #############
    // 初始化音乐播放器
    private fun initMusicPlayback() {
        mMusicPlaybackManager =
            MusicPlaybackManager(this, MusicPlayback(this))
        mMusicPlaybackManager!!.setPlaybackServiceCallback(object : PlaybackServiceCallback {
            override fun onPlayStateChanged(playbackState: PlaybackStateCompat) {
            }

            override fun onPlaybackProgress(position: Long, duration: Long, buffering: Long) {
            }

            override fun onError() {
            }

        })
    }

    // 初始化通知栏
    private fun initMusicNotification() {
        try {
            mMusicNotificationManager = MusicNotificationManager(this)
            mMusicNotificationManager!!.initNotification()
        } catch (e: RemoteException) {
            throw IllegalStateException("Could not create a MusicNotificationManager", e)
        }
    }

    // 初始化广播
    private fun initReceiver() {
        // TODO 初始化广播
    }
    // ############# 初始化 #############
}