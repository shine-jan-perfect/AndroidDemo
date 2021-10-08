package com.zuliz.musicplayerdemo.music.client.model

interface IMusicInfo {
    // 音频id
    var mediaId: String?

    // 类型：付费 or 免费
    var payType: String?

    //播放地址
    var trackSource: String?

    //合集名称
    var album: String?

    //作者
    var artist: String?

    //音频描述
    var displayDescription: String?

    // 返回 秒 数
    var duration: Long

    //
    var genre: String?

    //音频封面
    var artUrl: String?

    //合集封面
    var albumArtUrl: String?

    //音频名称
    var title: String?
}