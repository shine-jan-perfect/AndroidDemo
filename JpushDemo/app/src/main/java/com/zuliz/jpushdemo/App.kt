package com.zuliz.jpushdemo

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import cn.jpush.android.api.JPushInterface

class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
    }
}