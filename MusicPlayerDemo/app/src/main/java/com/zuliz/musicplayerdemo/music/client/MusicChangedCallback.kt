package com.zuliz.musicplayerdemo.music.client

import com.zuliz.musicplayerdemo.music.client.model.MusicInfo

interface MusicChangedCallback {
    fun onMetadataChanged(musicInfo: MusicInfo)
}