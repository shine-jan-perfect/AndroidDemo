package com.zuliz.musicplayerdemo.model

import android.os.Parcelable
import com.zuliz.musicplayerdemo.music.client.model.MusicInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlbumInfo(
    var id: Long? = null,
    var cover: String? = null,
    var title: String? = null,
    var description: String? = null,
    var data: MutableList<MusicInfo>? = null
) : Parcelable