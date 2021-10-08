package com.zuliz.musicplayerdemo.music.client

import android.content.ComponentName
import android.content.Context
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log
import com.zuliz.musicplayerdemo.music.client.utils.MusicPositionUtils
import com.zuliz.musicplayerdemo.music.client.model.MusicInfo
import com.zuliz.musicplayerdemo.music.client.model.IMusicInfo
import com.zuliz.musicplayerdemo.music.client.utils.MusicConvertUtils
import com.zuliz.musicplayerdemo.music.service.MusicPlaybackManager
import com.zuliz.musicplayerdemo.music.service.MusicService
import com.zuliz.musicplayerdemo.music.service.playback.MusicRepeatMode
import java.lang.Exception

class MusicManager {
    companion object {
        private const val TAG = "MusicManager"

        private var instance: MusicManager? = null

        fun getInstance(): MusicManager {
            if (instance == null) {
                synchronized(MusicManager::class.java) {
                    if (instance == null) instance = MusicManager()
                }
            }
            return instance!!
        }
    }

    // MediaBrowserCompat 客户端
    private var mMediaBrowser: MediaBrowserCompat? = null
    private var mMediaController: MediaControllerCompat? = null
    private var mTransportControls: MediaControllerCompat.TransportControls? = null

    private var mConnectionCallback: MediaBrowserCompat.ConnectionCallback? = null

    // ########### 回调到activity/fragment ###########
    private var mPlaybackState: PlaybackStateCompat? = null

    private var mMusicChangedCallback: MusicChangedCallback? = null
    private var mPlayingChangedCallback: PlayingChangedCallback? = null
    private var mProgressChangedCallback: ProgressChangedCallback? = null

    fun setMusicChangedCallback(callback: MusicChangedCallback) {
        mMusicChangedCallback = callback
    }

    fun setPlayingChangedCallback(callback: PlayingChangedCallback) {
        mPlayingChangedCallback = callback
    }

    fun setProgressChangedCallback(callback: ProgressChangedCallback) {
        mProgressChangedCallback = callback
    }

    private var mProgressHandler: Handler? = null
    private val mProgressRunnable: ProgressRunnable by lazy {
        ProgressRunnable()
    }

    private inner class ProgressRunnable : Runnable {
        private val TAG_PROGRESS_RUNNABLE = "ProgressRunnable"

        override fun run() {
            if (isPlaying()) {
                val timeDelta: Long =
                    SystemClock.elapsedRealtime() - getLastPositionUpdateTime()

                val currentProgress = (getCurrentPosition() + (timeDelta * getSpeed())).toInt()
                val bufferedProgress = getBufferedPosition().toInt()

                if (isAllowCallbackPosition()) {
                    mProgressChangedCallback?.onProgressChanged(currentProgress)
                }
                mProgressChangedCallback?.onBufferedProgressChanged(bufferedProgress)
            }
            mProgressHandler?.postDelayed(this, 100)
        }
    }

    init {
        mProgressHandler = Handler(Looper.getMainLooper())
        mProgressHandler?.post(mProgressRunnable)
    }

    private val mMediaControllerCallback = object : MediaControllerCompat.Callback() {
        private val TAG_MEDIA_CONTROLLER_CALLBACK = "MediaControllerCallback"

        override fun onSessionReady() {
            Log.d(TAG_MEDIA_CONTROLLER_CALLBACK, "onSessionReady()")
            super.onSessionReady()
        }

        override fun onSessionDestroyed() {
            Log.d(TAG_MEDIA_CONTROLLER_CALLBACK, "onSessionDestroyed()")
            super.onSessionDestroyed()
        }

        //自定义的事件回调，满足你各种自定义需求
        override fun onSessionEvent(event: String?, extras: Bundle?) {
            Log.d(TAG_MEDIA_CONTROLLER_CALLBACK, "onSessionEvent()")
            super.onSessionEvent(event, extras)
        }

        //额外信息回调，可以承载播放模式等信息
        override fun onExtrasChanged(extras: Bundle?) {
            Log.d(TAG_MEDIA_CONTROLLER_CALLBACK, "onExtrasChanged()")
            super.onExtrasChanged(extras)
        }

        //播放列表信息回调
        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            Log.d(TAG_MEDIA_CONTROLLER_CALLBACK, "onQueueChanged()")
            super.onQueueChanged(queue)
        }

        //歌曲信息回调
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.d(TAG_MEDIA_CONTROLLER_CALLBACK, "onMetadataChanged()")
            super.onMetadataChanged(metadata)
            val musicInfo = MusicConvertUtils.convertToIMusicInfo(MusicInfo(), metadata)
            if (musicInfo != null) {
                mMusicChangedCallback?.onMetadataChanged(musicInfo)

                val duration = MusicPositionUtils.formatSecond2MillisSecond(musicInfo.duration)
                val currentProgress = getCurrentPosition().toInt()
                val bufferedProgress = getBufferedPosition().toInt()

                mProgressChangedCallback?.onDurationChanged(duration)
                mProgressChangedCallback?.onBufferedProgressChanged(bufferedProgress)
                mProgressChangedCallback?.onProgressChanged(currentProgress)
            }
        }

        //播放状态信息回调
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.d(TAG_MEDIA_CONTROLLER_CALLBACK, "onPlaybackStateChanged()")
//            super.onPlaybackStateChanged(state)
            mPlaybackState = state

            mPlayingChangedCallback?.isPlaying(isPlaying())
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            Log.d(TAG_MEDIA_CONTROLLER_CALLBACK, "onRepeatModeChanged()")
            super.onRepeatModeChanged(repeatMode)
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            Log.d(TAG_MEDIA_CONTROLLER_CALLBACK, "onShuffleModeChanged()")
            super.onShuffleModeChanged(shuffleMode)
        }

    }
    // ########### 回调到activity/fragment ###########

    // ########### 初始化 ############ init() -> connect()
    fun init(context: Context) {
        if (!isValidPackage(context, context.packageName, Binder.getCallingUid())) {
            return
        }
        mMediaBrowser = MediaBrowserCompat(
            context,
            ComponentName(context, MusicService::class.java),
            getConnectionCallback(context),
            null
        )
        connect()
    }

    private fun connect() {
        if (mMediaBrowser == null) {
            return
        }
        if (!mMediaBrowser!!.isConnected) {
            try {
                mMediaBrowser!!.connect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun release() {
        mMediaController?.unregisterCallback(mMediaControllerCallback)
        if (mMediaBrowser != null) {
            if (mMediaBrowser!!.isConnected) {
                mMediaBrowser!!.disconnect()
            }
        }
        mProgressHandler?.removeCallbacks(mProgressRunnable)
        mProgressHandler?.removeCallbacksAndMessages(null)
    }

    private fun isValidPackage(context: Context, packageName: String?, callingUid: Int): Boolean {
        if (TextUtils.isEmpty(packageName)) {
            return false
        }
        val pm = context.packageManager
        val packages = pm.getPackagesForUid(callingUid)
        if (packages == null || packages.isEmpty()) {
            return false
        }
        packages.forEach {
            if (packageName.equals(it)) {
                return true
            }
        }
        return false
    }

    private fun getConnectionCallback(context: Context): MediaBrowserCompat.ConnectionCallback {
        mConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                Log.d(TAG, "onConnected()")
                try {
                    connectToSession(context, mMediaBrowser!!.sessionToken)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onConnectionSuspended() {
                Log.e(TAG, "onConnectionSuspended()")
            }

            override fun onConnectionFailed() {
                Log.e(TAG, "onConnectionFailed()")
            }
        }
        return mConnectionCallback!!
    }

    private fun connectToSession(context: Context, sessionToken: MediaSessionCompat.Token) {
        mMediaController = MediaControllerCompat(context, sessionToken)
        mMediaController!!.registerCallback(mMediaControllerCallback)
        mTransportControls = mMediaController!!.transportControls
    }
    // ########### 初始化 ############

    // ########### 播放控制 ############
    fun stop() {
        mTransportControls?.stop()
    }

    fun <T : IMusicInfo> playMusicList(musicList: MutableList<T>?, playIndex: Int = 0) {
        if (musicList == null || musicList.isEmpty()) {
            return
        }
        val args = Bundle().apply {
            putParcelableArrayList(
                MusicPlaybackManager.KEY_PLAY_MUSIC_LIST,
                MusicConvertUtils.convertToMediaMetadataList(musicList)
            )
            putInt(MusicPlaybackManager.KEY_PLAY_INDEX, playIndex)
        }
        mTransportControls?.sendCustomAction(
            MusicPlaybackManager.CUSTOM_ACTION_PLAY_MUSIC_LIST,
            args
        )
    }

    fun <T : IMusicInfo> addMusicList(musicList: MutableList<T>?) {
        if (musicList == null || musicList.isEmpty()) {
            return
        }
        val args = Bundle().apply {
            putParcelableArrayList(
                MusicPlaybackManager.KEY_ADD_MUSIC_LIST,
                MusicConvertUtils.convertToMediaMetadataList(musicList)
            )
        }
        mTransportControls?.sendCustomAction(
            MusicPlaybackManager.CUSTOM_ACTION_ADD_MUSIC_LIST,
            args
        )
    }

    fun playFromMediaId(mediaId: String) {
        mTransportControls?.playFromMediaId(mediaId, null)
    }

    fun skipToQueueItem(id: Long) {
        mTransportControls?.skipToQueueItem(id)
    }

    fun play() {
        mTransportControls?.play()
    }

    fun pause() {
        mTransportControls?.pause()
    }

    fun previous() {
        mTransportControls?.skipToPrevious()
    }

    fun next() {
        mTransportControls?.skipToNext()
    }

    fun seekTo(position: Long) {
        mTransportControls?.seekTo(position)
    }

    fun setSpeed(speed: Float) {
        mTransportControls?.setPlaybackSpeed(speed)
    }

    fun setRepeatMode(musicRepeatMode: MusicRepeatMode) {
        when (musicRepeatMode) {
            MusicRepeatMode.MEDIA_ALONE_LOOP -> mTransportControls?.setRepeatMode(
                PlaybackStateCompat.REPEAT_MODE_ONE
            )
            MusicRepeatMode.MEDIA_LIST_LOOP -> mTransportControls?.setRepeatMode(
                PlaybackStateCompat.REPEAT_MODE_ALL
            )
            MusicRepeatMode.MEDIA_LIST_ORDER_PLAY -> mTransportControls?.setRepeatMode(
                PlaybackStateCompat.REPEAT_MODE_NONE
            )
            else -> mTransportControls?.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
        }
    }

    private fun isAllowCallbackPosition(): Boolean {
        if (mPlaybackState == null) {
            return false
        }
        return mPlaybackState!!.state == PlaybackStateCompat.STATE_PLAYING
    }

    /**
     * 用于播放按钮判断(缓冲中和播放中都算播放中)
     */
    fun isPlaying(): Boolean {
        if (mPlaybackState == null) {
            return false
        }
        return mPlaybackState!!.state == PlaybackStateCompat.STATE_PLAYING || mPlaybackState!!.state == PlaybackStateCompat.STATE_BUFFERING
    }

    // 特殊处理不能使用 mMediaController!!.playbackState.position
    // 因为要使用 lastPositionUpdateTime 计算 position
    fun getCurrentPosition(): Long {
        if (mPlaybackState == null) {
            return 0
        }
        return mPlaybackState!!.position
    }

    // 特殊处理不能使用 mMediaController!!.playbackState.lastPositionUpdateTime
    // 因为要使用 lastPositionUpdateTime 计算 position
    fun getLastPositionUpdateTime(): Long {
        if (mPlaybackState == null) {
            return 0
        }
        return mPlaybackState!!.lastPositionUpdateTime
    }

    // 获取最新的 bufferedPosition
    fun getBufferedPosition(): Long {
        if (mPlaybackState == null) {
            return 0
        }
        return mMediaController!!.playbackState.bufferedPosition
    }

    fun getSpeed(): Float {
        if (mPlaybackState == null) {
            return 0F
        }
        return mPlaybackState!!.playbackSpeed
    }

    fun getCurrentPlayMediaId(): String? {
        if (mMediaController == null) {
            return null
        }
        val metadata = mMediaController!!.metadata ?: return null
        return metadata.description.mediaId
    }
    // ########### 播放控制 ############
}