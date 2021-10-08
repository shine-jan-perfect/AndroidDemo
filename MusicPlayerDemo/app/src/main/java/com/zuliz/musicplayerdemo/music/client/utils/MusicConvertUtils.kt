package com.zuliz.musicplayerdemo.music.client.utils

import android.support.v4.media.MediaMetadataCompat
import com.zuliz.musicplayerdemo.music.client.model.IMusicInfo
import java.util.ArrayList

object MusicConvertUtils {
    /**
     * 数据列表转化
     *
     * @param list
     * @param <T>
     * @return
    </T> */
    fun <T : IMusicInfo> convertToMediaMetadataList(list: List<T>): ArrayList<MediaMetadataCompat> {
        // 创建MediaMetadataCompat 播放队列
        val metaList = ArrayList<MediaMetadataCompat>(list.size)
        // 队列中添加数据
        for (item in list) {
            metaList.add(convertToMediaMetadata(item))
        }
        return metaList
    }

    /**
     * [IMusicInfo] 转为[MediaMetadataCompat]
     *
     * @param info
     * @return
     */
    private fun convertToMediaMetadata(info: IMusicInfo): MediaMetadataCompat {
        //
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, info.mediaId)
            .putString(MusicMetadataConstant.CUSTOM_METADATA_PAY_TYPE, info.payType)
            .putString(MusicMetadataConstant.CUSTOM_METADATA_TRACK_SOURCE, info.trackSource)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, info.album)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, info.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, info.displayDescription)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, info.duration)
            .putString(MediaMetadataCompat.METADATA_KEY_GENRE, info.genre)
            .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, info.artUrl)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, info.albumArtUrl)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, info.title)
            .build()
    }

    fun <T : IMusicInfo> convertToIMusicInfo(t: T?, metadata: MediaMetadataCompat?): T? {
        if (metadata == null) {
            return null
        }
        if (t == null) {
            return null
        }
        t.mediaId = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
        t.payType = metadata.getString(MusicMetadataConstant.CUSTOM_METADATA_PAY_TYPE)
        t.trackSource = metadata.getString(MusicMetadataConstant.CUSTOM_METADATA_TRACK_SOURCE)
        t.album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
        t.artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        t.displayDescription = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)
        t.duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
        t.genre = metadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE)
        t.artUrl = metadata.getString(MediaMetadataCompat.METADATA_KEY_ART_URI)
        t.albumArtUrl = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
        t.title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        return t
    }
}