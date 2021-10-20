package com.zuliz.jpushdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cn.jpush.android.api.JPushInterface

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        JPushInterface.resumePush(this)
    }
}