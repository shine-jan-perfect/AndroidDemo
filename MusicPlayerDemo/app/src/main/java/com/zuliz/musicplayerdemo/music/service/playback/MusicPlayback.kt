package com.zuliz.musicplayerdemo.music.service.playback

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.zuliz.musicplayerdemo.music.client.utils.MusicMetadataConstant
import java.io.IOException
import com.google.android.exoplayer2.Player


class MusicPlayback(context: Context) : IPlayback {
    companion object {
        private const val TAG = "MusicPlayback"
    }

    private var mContext: Context

    /**
     * 音频播放器
     */
    private var mExoPlayer: ExoPlayer? = null

    private var mPlaybackCallback: PlaybackCallback? = null

    private var mPlaybackState: Int? = null

    private var mMediaItemList = mutableListOf<MediaItem>()

    init {
        val applicationContext = context.applicationContext
        mContext = applicationContext
        initExoPlayer()
        mPlaybackState = PlaybackStateCompat.STATE_NONE
    }

    // ######## IPlayback ########
    // 设置播放回调函数
    override fun setPlaybackCallback(callback: PlaybackCallback) {
        mPlaybackCallback = callback
    }

    // 释放播放器
    override fun release() {
        releaseExoPlayer()
    }

    // 开始播放
    override fun start() {
        if (mExoPlayer == null) {
            return
        }
        mExoPlayer?.playWhenReady = true
    }

    // 停止播放
    override fun stop() {
        if (mExoPlayer == null) {
            return
        }
        mExoPlayer?.stop()
    }

    override fun playMusicList(
        mediaMetadataList: MutableList<MediaMetadataCompat>,
        playIndex: Int
    ) {
        if (mExoPlayer == null) {
            return
        }
        if (playIndex >= mediaMetadataList.size || playIndex < 0) {
            return
        }
        mMediaItemList.clear()
        mediaMetadataList.map {
            mMediaItemList.add(
                MediaItem.Builder()
                    .setUri(Uri.parse(it.getString(MusicMetadataConstant.CUSTOM_METADATA_TRACK_SOURCE)))
                    .setMediaId(it.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
                    .build()
            )
        }
        mExoPlayer!!.clearMediaItems()
        mExoPlayer!!.setMediaItems(mMediaItemList)
        mExoPlayer!!.prepare()
        mExoPlayer!!.seekToDefaultPosition(playIndex)
        mExoPlayer!!.playWhenReady = true
    }

    override fun addMusicList(
        mediaMetadataList: MutableList<MediaMetadataCompat>
    ) {
        if (mExoPlayer == null) {
            return
        }
        val mediaItemList = mutableListOf<MediaItem>()
        mediaMetadataList.map {
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(it.getString(MusicMetadataConstant.CUSTOM_METADATA_TRACK_SOURCE)))
                .setMediaId(it.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
                .build()
            mediaItemList.add(mediaItem)
            mMediaItemList.add(mediaItem)
        }
        mExoPlayer!!.addMediaItems(mediaItemList)
    }

    override fun playFromMediaId(mediaId: String?) {
        if (mExoPlayer == null) {
            return
        }
        if (TextUtils.isEmpty(mediaId)) {
            return
        }
        mMediaItemList.forEachIndexed { index, mediaItem ->
            if (mediaItem.mediaId == mediaId) {
                mExoPlayer!!.seekToDefaultPosition(index)
                mExoPlayer!!.playWhenReady = true
                return
            }
        }
    }

    override fun playOrPause() {
        if (mExoPlayer == null) {
            return
        }
        mExoPlayer!!.playWhenReady = !isPlaying()
    }

    override fun pause() {
        if (mExoPlayer == null) {
            return
        }
        mExoPlayer!!.playWhenReady = false
    }

    override fun seekTo(position: Long) {
        if (mExoPlayer == null) {
            return
        }
        if (position < 0 || position > getDuration()) {
            return
        }
        mExoPlayer!!.seekTo(position)
    }

    override fun previous() {
        if (mExoPlayer == null) {
            return
        }
        if (mExoPlayer!!.hasPrevious()) {
            mExoPlayer!!.previous()
        }
    }

    override fun next() {
        if (mExoPlayer == null) {
            return
        }
        if (mExoPlayer!!.hasNext()) {
            mExoPlayer!!.next()
        }
    }

    override fun setSpeed(speed: Float) {
        if (mExoPlayer == null) {
            return
        }
        val playbackParameters = mExoPlayer!!.playbackParameters
        mExoPlayer!!.setPlaybackParameters(playbackParameters.withSpeed(speed))
    }

    override fun setRepeatMode(musicRepeatMode: MusicRepeatMode) {
        if (mExoPlayer == null) {
            return
        }
        when (musicRepeatMode) {
            MusicRepeatMode.MEDIA_ALONE_LOOP -> mExoPlayer!!.repeatMode = Player.REPEAT_MODE_ONE
            MusicRepeatMode.MEDIA_LIST_LOOP -> mExoPlayer!!.repeatMode = Player.REPEAT_MODE_ALL
            MusicRepeatMode.MEDIA_LIST_ORDER_PLAY -> mExoPlayer!!.repeatMode =
                Player.REPEAT_MODE_OFF
            else -> mExoPlayer!!.repeatMode = Player.REPEAT_MODE_ALL
        }
    }
    // ######## IPlayback ########

    // ######## 从ExoPlayer获取信息 ########
    override fun isLoading(): Boolean {
        if (mExoPlayer == null) {
            return false
        }
        return mExoPlayer!!.playbackState == Player.STATE_BUFFERING && mExoPlayer!!.isLoading
    }

    override fun isPrepared(): Boolean {
        if (mExoPlayer == null) {
            return false
        }
        return mExoPlayer!!.playbackState != Player.STATE_IDLE
    }

    override fun isPlaying(): Boolean {
        if (mExoPlayer == null) {
            return false
        }
        return mExoPlayer!!.playbackState == Player.STATE_READY && mExoPlayer!!.playWhenReady
    }

    fun getCurrentMediaItem(): MediaItem? {
        if (mExoPlayer == null) {
            return null
        }
        return mExoPlayer!!.currentMediaItem
    }

    override fun getCurrentPosition(): Long {
        if (mExoPlayer == null) {
            return 0
        }
        return mExoPlayer!!.currentPosition
    }

    override fun getBufferedPosition(): Long {
        if (mExoPlayer == null) {
            return 0
        }
        return mExoPlayer!!.bufferedPosition
    }

    override fun getBufferedPercentage(): Int {
        if (mExoPlayer == null) {
            return 0
        }
        if (mExoPlayer!!.bufferedPercentage <= 0) {
            return 0
        }
        return mExoPlayer!!.bufferedPercentage
    }

    override fun getDuration(): Long {
        if (mExoPlayer == null) {
            return 0
        }
        if (mExoPlayer!!.duration <= 0) {
            return 0
        }
        return mExoPlayer!!.duration
    }

    override fun getSpeed(): Float {
        if (mExoPlayer == null) {
            return 0F
        }
        return mExoPlayer!!.playbackParameters.speed
    }
    // ######## 从ExoPlayer获取信息 ########

    // ######## 初始化 ExoPlayer ########
    private var mPlayEventListener: PlayerEventListener? = null

    private fun initExoPlayer() {
        if (mExoPlayer == null) {
            val extractorsFactory =
                DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)
            val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build()
            mExoPlayer = SimpleExoPlayer.Builder(mContext, extractorsFactory)
                .setAudioAttributes(audioAttributes, true)
                .setLoadControl(DefaultLoadControl())
                .build()
            mPlayEventListener = PlayerEventListener()
            mExoPlayer!!.addListener(mPlayEventListener!!)
        }
    }

    private fun releaseExoPlayer() {
        if (mPlayEventListener != null) {
            mExoPlayer?.removeListener(mPlayEventListener!!)
        }
        mExoPlayer?.stop()
        mExoPlayer?.release()
    }

    // 播放事件监听
    private inner class PlayerEventListener : Player.EventListener {
        private val TAG_EVENT = "PlayerEventListener"

        //播放器报错
        override fun onPlayerError(error: ExoPlaybackException) {
            Log.d(TAG_EVENT, "onPlayerError()")
            error.printStackTrace()
            super.onPlayerError(error)
            if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                val cause: IOException = error.sourceException
                // 判断是否为播放资源出错
                if (cause is HttpDataSource.HttpDataSourceException) {
                    val httpError: HttpDataSource.HttpDataSourceException = cause
                    val dataSpec = httpError.dataSpec

                    if (httpError is HttpDataSource.InvalidResponseCodeException) {
                        // 强制转换为InvalidResponseCodeException并检索返回的信息，响应头
                        // 就是说，当拿Uri去准备播放时，如果Uri缺少Http/Https开头等情况，就会在这里接收到异常
                        Log.e(TAG, httpError.responseMessage ?: "")
                    } else {
                        //尝试httpError.getCause()检索根本原因，请注意它可能为空
                        Log.e(TAG, httpError.cause?.message ?: "")
                    }
                }
            }
            mPlaybackCallback?.onError()
        }

        // 是否在加载
        override fun onIsLoadingChanged(isLoading: Boolean) {
            Log.d(TAG_EVENT, "onIsLoadingChanged() -> isLoading = $isLoading")
//            super.onIsLoadingChanged(isLoading)
            mPlaybackCallback?.onLoading(isLoading)
        }

        //参数改变
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            Log.d(TAG_EVENT, "onPlaybackParametersChanged()")
            super.onPlaybackParametersChanged(playbackParameters)
        }

        // 播放资源有改变
        override fun onStaticMetadataChanged(metadataList: MutableList<Metadata>) {
            Log.d(TAG_EVENT, "onStaticMetadataChanged()")
            super.onStaticMetadataChanged(metadataList)
        }

        // 播放总时间线改变，这里可用于设置播放总时长
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            Log.d(TAG_EVENT, "onTimelineChanged()")
            super.onTimelineChanged(timeline, reason)
        }

        // 播放位置中断
        override fun onPositionDiscontinuity(reason: Int) {
            Log.d(TAG_EVENT, "onPositionDiscontinuity()")
            super.onPositionDiscontinuity(reason)
        }

        // 播放受制，失去音频焦点
        override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
            Log.d(TAG_EVENT, "onPlaybackSuppressionReasonChanged()")
            super.onPlaybackSuppressionReasonChanged(playbackSuppressionReason)
        }

        //播放器播放状态改变，查看 State 有IDLE，BUFFERING加载中， READY 资源准备好， ENDED 已结束
        override fun onPlaybackStateChanged(state: Int) {
            Log.d(TAG_EVENT, "onPlaybackStateChanged()")
//            super.onPlaybackStateChanged(state)
            when (state) {
                Player.STATE_IDLE -> {
                    //播放器没有可播放的媒体。
                    mPlaybackState = PlaybackStateCompat.STATE_NONE
                }
                Player.STATE_BUFFERING -> {
                    //播放器无法立即从当前位置开始播放。这种状态通常需要加载更多数据时发生。
                    mPlaybackState = PlaybackStateCompat.STATE_BUFFERING
                }
                Player.STATE_READY -> {
                    // 播放器可以立即从当前位置开始播放。如果{@link#getPlayWhenReady（）}为true，否则暂停。
                    //当点击暂停或者播放时都会调用此方法
                    //当跳转进度时，进度加载完成后调用此方法
                    mPlaybackState = if (isPlaying()) {
                        PlaybackStateCompat.STATE_PLAYING
                    } else {
                        PlaybackStateCompat.STATE_PAUSED
                    }
                }
                Player.STATE_ENDED -> {
                    //播放器完成了播放
                    mPlaybackState = PlaybackStateCompat.STATE_STOPPED
                }
            }
            mPlaybackCallback?.onPlayStateChanged(mPlaybackState!!)
        }

        //视频资源准备好就播放的设置改变
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            Log.d(TAG_EVENT, "onPlayWhenReadyChanged()")
            super.onPlayWhenReadyChanged(playWhenReady, reason)
        }

        //播放状态改变，开始播放或暂停
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Log.d(TAG_EVENT, "onIsPlayingChanged()")
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                mPlaybackCallback?.onPlayStateChanged(PlaybackStateCompat.STATE_PLAYING)
            } else {
                mPlaybackCallback?.onPlayStateChanged(PlaybackStateCompat.STATE_PAUSED)
            }
        }

        //重复播放的模式改变
        override fun onRepeatModeChanged(repeatMode: Int) {
            Log.d(TAG_EVENT, "onRepeatModeChanged()")
            super.onRepeatModeChanged(repeatMode)
        }

        // 洗牌
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            Log.d(TAG_EVENT, "onRepeatModeChanged()")
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
        }

        // 播放曲目变化或者重复播放该曲目
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            Log.d(TAG_EVENT, "onMediaItemTransition()")
            // TODO media item transition
//            super.onMediaItemTransition(mediaItem, reason)
            mPlaybackCallback?.onMetadataChanged(mediaItem)
            mPlaybackCallback?.onPlayStateChanged(mPlaybackState!!)
        }

        // 换音轨
        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            Log.d(TAG_EVENT, "onTracksChanged()")
            super.onTracksChanged(trackGroups, trackSelections)
        }

        // 包含所有事件
        override fun onEvents(player: Player, events: Player.Events) {
//            Log.d(TAG_EVENT, "onEvents()")
            super.onEvents(player, events)
        }

    }
    // ######## 初始化 ExoPlayer ########
}