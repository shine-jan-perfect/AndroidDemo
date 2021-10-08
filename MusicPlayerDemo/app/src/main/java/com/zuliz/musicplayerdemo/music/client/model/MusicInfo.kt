package com.zuliz.musicplayerdemo.music.client.model

import android.os.Parcelable
import com.zuliz.musicplayerdemo.music.client.model.IMusicInfo
import kotlinx.parcelize.Parcelize

@Parcelize
class MusicInfo(
    override var mediaId: String? = null,
    override var payType: String? = null,
    override var trackSource: String? = null,
    override var album: String? = null,
    override var artist: String? = null,
    override var displayDescription: String? = null,
    override var duration: Long = 0L, // 单位秒
    override var genre: String? = null,
    override var artUrl: String? = null,
    override var albumArtUrl: String? = null,
    override var title: String? = null
) : IMusicInfo, Parcelable