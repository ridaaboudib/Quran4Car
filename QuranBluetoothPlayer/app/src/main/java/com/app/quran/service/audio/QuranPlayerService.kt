package com.app.quran.service.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media3.session.MediaSession
import com.app.quran.MainActivity
import com.app.quran.QuranBluetoothApp
import com.app.quran.R
import com.app.quran.data.local.preferences.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuranPlayerService : Service() {

    @Inject
    lateinit var audioPlayerManager: AudioPlayerManager

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var mediaSession: MediaSession? = null

    companion object {
        private const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "quran_player_channel"
        const val CHANNEL_NAME = "Quran Player"

        private const val ACTION_PLAY = "com.app.quran.ACTION_PLAY"
        private const val ACTION_PAUSE = "com.app.quran.ACTION_PAUSE"
        private const val ACTION_STOP = "com.app.quran.ACTION_STOP"
        private const val ACTION_NEXT = "com.app.quran.ACTION_NEXT"
        private const val ACTION_PREVIOUS = "com.app.quran.ACTION_PREVIOUS"
        private const val ACTION_SAVE_POSITION = "com.app.quran.ACTION_SAVE_POSITION"

        private const val EXTRA_SURAH_ID = "surah_id"
        private const val EXTRA_POSITION = "position"

        fun start(context: Context) {
            val intent = Intent(context, QuranPlayerService::class.java).apply {
                action = ACTION_PLAY
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun startWithResume(context: Context, surahId: Int?, positionMs: Long?) {
            val intent = Intent(context, QuranPlayerService::class.java).apply {
                action = ACTION_PLAY
                surahId?.let { putExtra(EXTRA_SURAH_ID, it) }
                positionMs?.let { putExtra(EXTRA_POSITION, it) }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun pause(context: Context) {
            val intent = Intent(context, QuranPlayerService::class.java).apply {
                action = ACTION_PAUSE
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, QuranPlayerService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun savePositionAndPause(context: Context) {
            val intent = Intent(context, QuranPlayerService::class.java).apply {
                action = ACTION_SAVE_POSITION
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        audioPlayerManager.initialize()
        setupMediaSession()
        createNotificationChannel()
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "QuranPlayer").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    audioPlayerManager.play()
                    updateNotification()
                }

                override fun onPause() {
                    audioPlayerManager.pause()
                    updateNotification()
                }

                override fun onSkipToNext() {
                    audioPlayerManager.skipToNext()
                }

                override fun onSkipToPrevious() {
                    audioPlayerManager.skipToPrevious()
                }

                override fun onSeekTo(pos: Long) {
                    audioPlayerManager.seekTo(pos)
                }

                override fun onStop() {
                    audioPlayerManager.stop()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            })
            isActive = true
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Quran audio playback with smart notifications"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                startForeground(NOTIFICATION_ID, createNotification())
                
                val surahId = intent.getIntExtra(EXTRA_SURAH_ID, -1)
                val position = intent.getLongExtra(EXTRA_POSITION, -1L)
                
                if (surahId != -1 && position != -1L) {
                    // Resume from saved position
                    audioPlayerManager.playRandomSurah() // For now, just play random
                } else {
                    audioPlayerManager.playRandomSurah()
                }
            }
            ACTION_PAUSE -> {
                audioPlayerManager.pause()
                updateNotification()
            }
            ACTION_STOP -> {
                audioPlayerManager.stop()
                mediaSession?.isActive = false
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_NEXT -> {
                audioPlayerManager.skipToNext()
                updateNotification()
            }
            ACTION_PREVIOUS -> {
                audioPlayerManager.skipToPrevious()
                updateNotification()
            }
            ACTION_SAVE_POSITION -> {
                val state = audioPlayerManager.playerState.value
                state.currentSurah?.let { surah ->
                    val position = audioPlayerManager.getCurrentPosition()
                    preferencesManager.lastSurahId = surah.id
                    // Save to BluetoothHelper via context would need refactoring
                }
                audioPlayerManager.pause()
                updateNotification()
            }
        }

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val state = audioPlayerManager.playerState.value
        val currentSurah = state.currentSurah

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (state.isPlaying) {
            NotificationCompat.Action(
                R.drawable.ic_launcher_foreground,
                "Pause",
                createPendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_launcher_foreground,
                "Play",
                createPendingIntent(ACTION_PLAY)
            )
        }

        val previousAction = NotificationCompat.Action(
            R.drawable.ic_launcher_foreground,
            "Previous",
            createPendingIntent(ACTION_PREVIOUS)
        )

        val nextAction = NotificationCompat.Action(
            R.drawable.ic_launcher_foreground,
            "Next",
            createPendingIntent(ACTION_NEXT)
        )

        val sleepTimerState = audioPlayerManager.sleepTimerState.value
        val sleepTimerText = if (sleepTimerState.isActive) {
            " • Sleep in ${sleepTimerState.remainingMinutes} min"
        } else ""

        val contentText = if (currentSurah != null) {
            "${currentSurah.nameArabic} - ${currentSurah.name}$sleepTimerText"
        } else {
            "جاري تشغيل القرآن الكريم..."
        }

        val bigContentText = if (currentSurah != null) {
            val position = formatDuration(state.currentPositionMs)
            val duration = formatDuration(state.duration)
            "$position / $duration"
        } else {
            "اضغط للاستماع"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("مصحف البلوثوث")
            .setContentText(contentText)
            .setSubText(bigContentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(state.isPlaying)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, QuranPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerManager.release()
        mediaSession?.release()
    }
}