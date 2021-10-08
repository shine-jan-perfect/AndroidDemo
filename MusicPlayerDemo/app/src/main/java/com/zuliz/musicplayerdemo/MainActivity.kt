package com.zuliz.musicplayerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.zuliz.musicplayerdemo.music.client.MusicManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zuliz.musicplayerdemo.model.AlbumInfo
import com.zuliz.musicplayerdemo.music.client.model.MusicInfo

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.rv)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = MainAdapter(testAlbumInfoList).also { mAdapter = it }
        mAdapter.setOnItemClickListener(object : MainAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                PlayActivity.start(
                    this@MainActivity,
                    testAlbumInfoList[position].data as ArrayList<MusicInfo>
                )
            }
        })

        initMusicManager()
    }

    private fun initMusicManager() {
        MusicManager.getInstance().init(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicManager.getInstance().release()
    }

    internal class MainAdapter(private val mDatas: List<AlbumInfo>) :
        RecyclerView.Adapter<MainAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_main, parent, false
            )
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            Glide.with(holder.itemView.context).load(mDatas[position].cover)
                .into(holder.ivCover)
            holder.tvTitle.text = mDatas[position].title
            holder.tvDescription.text = mDatas[position].description

            holder.itemView.setOnClickListener {
                mOnItemClickListener?.onItemClick(position)
            }
        }

        override fun getItemCount(): Int {
            return mDatas.size
        }

        internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var ivCover: ImageView = view.findViewById(R.id.iv_cover)
            var tvTitle: TextView = view.findViewById(R.id.tv_title)
            var tvDescription: TextView = view.findViewById(R.id.tv_description)
        }

        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }

        private var mOnItemClickListener: OnItemClickListener? = null

        fun setOnItemClickListener(listener: OnItemClickListener) {
            mOnItemClickListener = listener
        }

    }
}