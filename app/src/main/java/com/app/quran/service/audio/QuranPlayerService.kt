package com.app.quran.service.audio

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuranPlayerService : Service() {

    companion object {
        private const val TAG = "QuranPlayerService"
        const val ACTION_START_PLAYING = "ACTION_START_PLAYING"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
    }

    @Inject
    lateinit var player: ExoPlayer

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): QuranPlayerService = this@QuranPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_START_PLAYING -> {
                startPlaying()
            }
            ACTION_PAUSE -> {
                player.pause()
            }
            ACTION_RESUME -> {
                player.play()
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun startPlaying() {
        Log.d(TAG, "Starting random surah playback")
        try {
            val mediaItem = MediaItem.fromUri("https://example.com/surah-001.mp3")
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting playback", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }
}
