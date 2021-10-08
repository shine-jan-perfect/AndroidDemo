package com.zuliz.musicplayerdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.zuliz.musicplayerdemo.music.client.model.MusicInfo
import com.zuliz.musicplayerdemo.music.client.MusicChangedCallback
import com.zuliz.musicplayerdemo.music.client.MusicManager
import com.zuliz.musicplayerdemo.music.client.PlayingChangedCallback
import com.zuliz.musicplayerdemo.music.client.ProgressChangedCallback
import com.zuliz.musicplayerdemo.music.service.playback.MusicRepeatMode
import com.zuliz.musicplayerdemo.widget.MusicSeekBar

class PlayActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context, musicInfoList: ArrayList<MusicInfo>) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putParcelableArrayListExtra("musicInfoList", musicInfoList)
            context.startActivity(intent)
        }
    }

    private var mIvCover: ImageView? = null
    private var mTvTitle: TextView? = null
    private var mTvDescription: TextView? = null
    private var mMusicSeekBar: MusicSeekBar? = null
    private var mBtnPlayOrPause: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val musicInfoList = intent.extras!!.getParcelableArrayList<MusicInfo>("musicInfoList")

        mIvCover = findViewById(R.id.iv_cover)
        mTvTitle = findViewById(R.id.tv_title)
        mTvDescription = findViewById(R.id.tv_description)
        mBtnPlayOrPause = findViewById(R.id.btn_play_pause)
        mMusicSeekBar = findViewById(R.id.music_seek_bar)

        Glide.with(this).load(musicInfoList!![0].albumArtUrl)
            .into(mIvCover!!)
        mTvTitle!!.text = musicInfoList[0].title
        mTvDescription!!.text = musicInfoList[0].displayDescription

        MusicManager.getInstance().setRepeatMode(MusicRepeatMode.MEDIA_LIST_LOOP)
        MusicManager.getInstance().playMusicList(
            musicInfoList,
            0
        )

        findViewById<Button>(R.id.btn_play_all_in_order).setOnClickListener {
            MusicManager.getInstance().playFromMediaId(
                musicInfoList[0].mediaId!!
            )
        }
        findViewById<Button>(R.id.btn_play_all_in_order_loop).setOnClickListener {
            MusicManager.getInstance().addMusicList(
                testAlbumInfoList[2].data
            )
        }

        mMusicSeekBar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = if (seekBar?.progress == null) {
                    0L
                } else {
                    seekBar.progress.toLong()
                }
                MusicManager.getInstance().seekTo(progress)
            }
        })

        findViewById<Button>(R.id.btn_previous).setOnClickListener {
            MusicManager.getInstance().previous()
        }

        mBtnPlayOrPause!!.setOnClickListener {
            if (MusicManager.getInstance().isPlaying()) {
                MusicManager.getInstance().pause()
            } else {
                MusicManager.getInstance().play()
            }
        }
        findViewById<Button>(R.id.btn_next).setOnClickListener {
            MusicManager.getInstance().next()
        }

        MusicManager.getInstance().setMusicChangedCallback(object : MusicChangedCallback {
            private val TAG_METADATA_CHANGED = "MetadataChangedCallback"

            override fun onMetadataChanged(musicInfo: MusicInfo) {
                if (mIvCover != null) {
                    Glide.with(this@PlayActivity).load(musicInfo.albumArtUrl)
                        .into(mIvCover!!)
                }
                mTvTitle?.text = musicInfo.title
                mTvDescription?.text = musicInfo.displayDescription
            }
        })
        MusicManager.getInstance().setPlayingChangedCallback(object : PlayingChangedCallback {
            override fun isPlaying(isPlaying: Boolean) {
                if (isPlaying) {
                    mBtnPlayOrPause?.text = "暂停"
                } else {
                    mBtnPlayOrPause?.text = "播放"
                }
            }

        })
        MusicManager.getInstance().setProgressChangedCallback(object : ProgressChangedCallback {
            override fun onDurationChanged(duration: Int) {
                mMusicSeekBar?.max = duration
                Log.d("durationChanged", "duration = $duration")
            }

            override fun onProgressChanged(currentProgress: Int) {
                mMusicSeekBar?.progress = currentProgress
                Log.d("progressChanged", "currentProgress = $currentProgress")
            }

            override fun onBufferedProgressChanged(bufferedProgress: Int) {
                mMusicSeekBar?.secondaryProgress = bufferedProgress
                Log.d("bufferedProgressChanged", "bufferedProgress = $bufferedProgress")
            }

        })
    }
}