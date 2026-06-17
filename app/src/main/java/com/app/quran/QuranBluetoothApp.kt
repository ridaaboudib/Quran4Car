package com.app.quran

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuranBluetoothApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
