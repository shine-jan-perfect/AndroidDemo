package com.zuliz.musicplayerdemo

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class App : Application() {
    override fun attachBaseContext(base: Context?) {
        MultiDex.install(this)
        super.attachBaseContext(base)
    }
}