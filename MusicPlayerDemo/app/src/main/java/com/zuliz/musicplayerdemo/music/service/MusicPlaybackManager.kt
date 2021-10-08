package com.zuliz.musicplayerdemo.music.service

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.exoplayer2.MediaItem
import com.zuliz.musicplayerdemo.music.service.playback.MusicPlayback
import com.zuliz.musicplayerdemo.music.service.playback.MusicRepeatMode
import com.zuliz.musicplayerdemo.music.service.playback.PlaybackCallback
import java.lang.Exception

class MusicPlaybackManager(
    musicService: MusicService,
    private val musicPlayback: MusicPlayback
) {
    companion object {
        private const val TAG = "MusicPlaybackManager"

        // action
        const val CUSTOM_ACTION_PLAY_MUSIC_LIST =
            "com.zuliz.lib_music_player.action.PLAY_MUSIC_LIST"

        // key
        const val KEY_PLAY_MUSIC_LIST = "com.zuliz.lib_music_player.key.PLAY_MUSIC_LIST"
        const val KEY_PLAY_INDEX = "com.zuliz.lib_music_player.key.PLAY_INDEX"

        // action
        const val CUSTOM_ACTION_ADD_MUSIC_LIST =
            "com.zuliz.lib_music_player.action.ADD_MUSIC_LIST"

        // key
        const val KEY_ADD_MUSIC_LIST = "com.zuliz.lib_music_player.key.ADD_MUSIC_LIST"
    }

    private var mMediaMetadataList = mutableListOf<MediaMetadataCompat>()

    // 初始化 MediaSession
    var mMediaSession: MediaSessionCompat? = null

    init {
        initMediaSession(musicService)
    }

    private fun initMediaSession(musicService: MusicService) {
        mMediaSession = try {
            // 创建MediaSessionCompat
            MediaSessionCompat(musicService, MusicService.TAG)
        } catch (e: Exception) {
            null
        }
        try {
            if (mMediaSession == null) {
                mMediaSession = MediaSessionCompat(
                    musicService,
                    MusicService.TAG,
                    ComponentName(
                        musicService,
                        "android.support.v4.media.session.MediaButtonReceiver"
                    ),
                    null
                )
            }
        } catch (e: Exception) {
            mMediaSession = null
        }

        if (mMediaSession == null) {
            return
        }
        // 获取并设置token
        musicService.sessionToken = mMediaSession!!.sessionToken
        // 用户通过MediaControllerCompat对UI的操作，
        // 会通过MediaSessionCompat.Callback 回调到Service端，
        // 来操纵“播放器”进行播放、暂定、快进、上一曲、下一曲等操作
        mMediaSessionCallback = MediaSessionCallback()
        mMediaSession!!.setCallback(mMediaSessionCallback)
        try {
            // Exoplayer 内部已经实现线控和音频焦点
            mMediaSession!!.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or  // 线控
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS // 回调
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mMediaSession!!.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0F)
                .build()
        )
    }

    fun release() {
        // music playback
        musicPlayback.release()
        // media session
        mMediaSession?.isActive = false
        mMediaSession?.release()
    }

    // ############# PlaybackServiceCallback ##############
    private var mPlaybackServiceCallback: PlaybackServiceCallback? = null

    fun setPlaybackServiceCallback(playbackServiceCallback: PlaybackServiceCallback) {
        mPlaybackServiceCallback = playbackServiceCallback
    }
    // ############# PlaybackServiceCallback ##############

    // ############## Playback Listener ##############
    private var mMusicPlaybackListener: MusicPlaybackCallback? = null

    init {
        mMusicPlaybackListener = MusicPlaybackCallback()
        musicPlayback.setPlaybackCallback(mMusicPlaybackListener!!)
    }

    private inner class MusicPlaybackCallback : PlaybackCallback {
        private val TAG_MUSIC_PLAYBACK_CALLBACK = "MusicPlaybackCallback"

        override fun onLoading(isLoading: Boolean) {}

        override fun onPlayStateChanged(@PlaybackStateCompat.State state: Int) {
            val playbackState = PlaybackStateCompat.Builder()
                .setBufferedPosition(musicPlayback.getBufferedPosition())
                .setState(
                    state,
                    musicPlayback.getCurrentPosition(),
                    musicPlayback.getSpeed()
                ).build()
            mMediaSession?.setPlaybackState(playbackState)
            Log.d(
                TAG_MUSIC_PLAYBACK_CALLBACK,
                "lastPositionUpdateTime = ${playbackState.lastPositionUpdateTime}"
            )
            mPlaybackServiceCallback?.onPlayStateChanged(playbackState)
        }

        override fun onMetadataChanged(mediaItem: MediaItem?) {
            // 设置 metadata 回调给客户端
            if (mediaItem == null) {
                return
            }
            mMediaMetadataList.forEach {
                if (it.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) == mediaItem.mediaId) {
                    mMediaSession?.setMetadata(it)
                    return@forEach
                }
            }
        }

        override fun onPlaybackProgress(position: Long, duration: Long, buffering: Long) {
            mPlaybackServiceCallback?.onPlaybackProgress(position, duration, buffering)
        }

        override fun onError() {
            mPlaybackServiceCallback?.onError()
        }
    }
    // ############## Playback Listener ##############

    // ############## Media Session Callback ##############
    private var mMediaSessionCallback: MediaSessionCallback? = null

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onPrepare() {
            Log.d(TAG, "onPrepare()")
            super.onPrepare()
            musicPlayback.start()
        }

        override fun onStop() {
            Log.d(TAG, "onStop()")
            super.onStop()
            musicPlayback.stop()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            Log.d(TAG, "onPlayFromMediaId()")
            super.onPlayFromMediaId(mediaId, extras)
            musicPlayback.playFromMediaId(mediaId)
        }

        override fun onPlay() {
            Log.d(TAG, "onPlay()")
            super.onPlay()
            musicPlayback.playOrPause()
        }

        override fun onPause() {
            Log.d(TAG, "onPause()")
            super.onPause()
            musicPlayback.playOrPause()
        }

        override fun onSkipToPrevious() {
            Log.d(TAG, "onSkipToPrevious()")
            super.onSkipToPrevious()
            musicPlayback.previous()
        }

        override fun onSkipToNext() {
            Log.d(TAG, "onSkipToNext()")
            super.onSkipToNext()
            musicPlayback.next()
        }

        override fun onSeekTo(pos: Long) {
            Log.d(TAG, "onSeekTo()")
            super.onSeekTo(pos)
            musicPlayback.seekTo(pos)
        }

        override fun onSetPlaybackSpeed(speed: Float) {
            Log.d(TAG, "onSetPlaybackSpeed()")
            super.onSetPlaybackSpeed(speed)
            musicPlayback.setSpeed(speed)
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            Log.d(TAG, "onSetRepeatMode()")
            super.onSetRepeatMode(repeatMode)
            when (repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_ONE -> musicPlayback.setRepeatMode(
                    MusicRepeatMode.MEDIA_ALONE_LOOP
                )
                PlaybackStateCompat.REPEAT_MODE_ALL -> musicPlayback.setRepeatMode(
                    MusicRepeatMode.MEDIA_LIST_LOOP
                )
                PlaybackStateCompat.REPEAT_MODE_NONE -> musicPlayback.setRepeatMode(
                    MusicRepeatMode.MEDIA_LIST_ORDER_PLAY
                )
                else -> musicPlayback.setRepeatMode(MusicRepeatMode.MEDIA_LIST_LOOP)
            }
        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            Log.d(TAG, "onCustomAction()")
            if (action == null) return
            handleCustomAction(action, extras)
        }
    }

    // ############## Handle Media Session Callback ##############
    private fun handleCustomAction(action: String, extras: Bundle?) {
        when (action) {
            CUSTOM_ACTION_PLAY_MUSIC_LIST -> {
                handlePlayMusicList(extras)
            }
            CUSTOM_ACTION_ADD_MUSIC_LIST -> {
                handleAddMusicList(extras)
            }
        }
    }

    private fun handlePlayMusicList(extras: Bundle?) {
        if (extras == null) {
            return
        }
        val mediaMetadataList: MutableList<MediaMetadataCompat> =
            extras.getParcelableArrayList(KEY_PLAY_MUSIC_LIST)
                ?: return
        val playIndex = extras.getInt(KEY_PLAY_INDEX, -1)
        mMediaMetadataList = mediaMetadataList
        musicPlayback.playMusicList(mediaMetadataList, playIndex)
    }

    private fun handleAddMusicList(extras: Bundle?) {
        if (extras == null) {
            return
        }
        val mediaMetadataList: MutableList<MediaMetadataCompat> =
            extras.getParcelableArrayList(KEY_ADD_MUSIC_LIST)
                ?: return
        mMediaMetadataList.addAll(mediaMetadataList)
        musicPlayback.addMusicList(mediaMetadataList)
    }
    // ############## Handle Media Session Callback ##############
    // ############## Media Session Callback ##############
}