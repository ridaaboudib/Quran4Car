package com.app.quran.service.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.app.quran.QuranBluetoothApp
import com.app.quran.R
import com.app.quran.data.local.db.dao.DownloadDao
import com.app.quran.data.local.db.entity.DownloadEntity
import com.app.quran.data.local.db.entity.DownloadStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.io.File
import java.net.URL
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : Service() {

    @Inject
    lateinit var downloadDao: DownloadDao

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var downloadJob: Job? = null

    companion object {
        private const val NOTIFICATION_ID = 2
        const val CHANNEL_ID = "download_channel"
        const val CHANNEL_NAME = "Downloads"

        const val ACTION_START_DOWNLOAD = "com.app.quran.download.START"
        const val ACTION_PAUSE_DOWNLOAD = "com.app.quran.download.PAUSE"
        const val ACTION_CANCEL_DOWNLOAD = "com.app.quran.download.CANCEL"

        const val EXTRA_READER_ID = "reader_id"
        const val EXTRA_SURAH_IDS = "surah_ids" // comma-separated

        fun startDownload(context: Context, readerId: Int, surahIds: List<Int>? = null) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_START_DOWNLOAD
                putExtra(EXTRA_READER_ID, readerId)
                surahIds?.let { putExtra(EXTRA_SURAH_IDS, it.joinToString(",")) }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun pauseDownload(context: Context) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_PAUSE_DOWNLOAD
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Download progress for Quran audio"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_DOWNLOAD -> {
                val readerId = intent.getIntExtra(EXTRA_READER_ID, 1)
                val surahIdsStr = intent.getStringExtra(EXTRA_SURAH_IDS)
                val surahIds = surahIdsStr?.split(",")?.mapNotNull { it.toIntOrNull() }

                startForeground(NOTIFICATION_ID, createNotification("جاري تحميل القرآن...", 0))
                startDownloadProcess(readerId, surahIds)
            }
            ACTION_PAUSE_DOWNLOAD -> {
                pauseDownloadProcess()
            }
            ACTION_CANCEL_DOWNLOAD -> {
                cancelDownloadProcess()
            }
        }

        return START_NOT_STICKY
    }

    private fun startDownloadProcess(readerId: Int, surahIds: List<Int>?) {
        downloadJob = scope.launch {
            try {
                val downloads = if (surahIds != null) {
                    downloadDao.getDownloadsByReader(readerId).first()
                        .filter { it.surahId in surahIds }
                } else {
                    downloadDao.getPendingDownloads().first()
                }

                var completed = 0
                val total = downloads.size

                for (download in downloads) {
                    if (!isActive) break

                    // Update status to downloading
                    downloadDao.updateDownloadProgress(
                        download.surahId,
                        download.readerId,
                        DownloadStatus.DOWNLOADING.name,
                        0
                    )

                    // Simulate download (in real app, would download from server)
                    for (progress in 0..100 step 10) {
                        if (!isActive) break
                        downloadDao.updateDownloadProgress(
                            download.surahId,
                            download.readerId,
                            DownloadStatus.DOWNLOADING.name,
                            progress
                        )
                        updateNotification("جاري تحميل السورة ${download.surahId}...", progress)
                        delay(200)
                    }

                    // Mark as completed
                    val localPath = "files/quran/${download.readerId}/surah_${"%03d".format(download.surahId)}.mp3"
                    downloadDao.markDownloadCompleted(download.surahId, download.readerId, localPath)

                    completed++
                    val overallProgress = (completed * 100) / total
                    updateNotification("تم تحميل $completed من $total", overallProgress)
                }

                updateNotification("اكتمل التنزيل!", 100)
                delay(2000)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()

            } catch (e: Exception) {
                updateNotification("حدث خطأ في التنزيل", 0)
                delay(3000)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun pauseDownloadProcess() {
        downloadJob?.cancel()
        updateNotification("تم إيقاف التنزيل مؤقتاً", 0)
    }

    private fun cancelDownloadProcess() {
        downloadJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(text: String, progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("تحميل القرآن الكريم")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(text: String, progress: Int) {
        val notification = createNotification(text, progress)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadJob?.cancel()
        scope.cancel()
    }
}