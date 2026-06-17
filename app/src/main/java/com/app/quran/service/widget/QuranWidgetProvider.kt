package com.app.quran.service.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.app.quran.MainActivity
import com.app.quran.R
import com.app.quran.service.audio.QuranPlayerService

class QuranWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.app.quran.widget.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.app.quran.widget.ACTION_NEXT"
        const val ACTION_OPEN_APP = "com.app.quran.widget.ACTION_OPEN_APP"

        fun updateWidget(context: Context) {
            val intent = Intent(context, QuranWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, QuranWidgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }

        fun updateWidgetWithState(
            context: Context,
            surahName: String,
            readerName: String,
            isPlaying: Boolean,
            progress: Float,
            positionText: String,
            durationText: String
        ) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(context, QuranWidgetProvider::class.java)
            )

            for (appWidgetId in ids) {
                val views = RemoteViews(context.packageName, R.layout.widget_quran_player)

                // Update text
                views.setTextViewText(R.id.widget_surah_name, surahName)
                views.setTextViewText(R.id.widget_reader_name, readerName)
                views.setTextViewText(R.id.widget_progress_text, "$positionText / $durationText")

                // Update play/pause button
                val playPauseIcon = if (isPlaying) {
                    android.R.drawable.ic_media_pause
                } else {
                    android.R.drawable.ic_media_play
                }
                views.setImageViewResource(R.id.widget_play_pause, playPauseIcon)

                // Update progress bar
                views.setProgressBar(R.id.widget_progress_bar, 100, (progress * 100).toInt(), false)

                // Set click intents
                val openAppIntent = Intent(context, MainActivity::class.java)
                val openAppPendingIntent = PendingIntent.getActivity(
                    context, 0, openAppIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_container, openAppPendingIntent)

                val playPauseIntent = Intent(context, QuranWidgetProvider::class.java).apply {
                    action = ACTION_PLAY_PAUSE
                }
                val playPausePendingIntent = PendingIntent.getBroadcast(
                    context, 1, playPauseIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_play_pause, playPausePendingIntent)

                val nextIntent = Intent(context, QuranWidgetProvider::class.java).apply {
                    action = ACTION_NEXT
                }
                val nextPendingIntent = PendingIntent.getBroadcast(
                    context, 2, nextIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_next, nextPendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_quran_player)

            // Default state
            views.setTextViewText(R.id.widget_surah_name, "اختر سورة")
            views.setTextViewText(R.id.widget_reader_name, "الشيخ مصطفى الفرجاني")
            views.setTextViewText(R.id.widget_progress_text, "00:00 / 00:00")
            views.setProgressBar(R.id.widget_progress_bar, 100, 0, false)
            views.setImageViewResource(R.id.widget_play_pause, android.R.drawable.ic_media_play)

            // Set click intents
            val openAppIntent = Intent(context, MainActivity::class.java)
            val openAppPendingIntent = PendingIntent.getActivity(
                context, 0, openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, openAppPendingIntent)

            val playPauseIntent = Intent(context, QuranWidgetProvider::class.java).apply {
                action = ACTION_PLAY_PAUSE
            }
            val playPausePendingIntent = PendingIntent.getBroadcast(
                context, 1, playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_play_pause, playPausePendingIntent)

            val nextIntent = Intent(context, QuranWidgetProvider::class.java).apply {
                action = ACTION_NEXT
            }
            val nextPendingIntent = PendingIntent.getBroadcast(
                context, 2, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_next, nextPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                // Toggle play/pause - would need to communicate with service
                // For now, start the service
                QuranPlayerService.start(context)
            }
            ACTION_NEXT -> {
                // Skip to next - would need to communicate with service
                QuranPlayerService.start(context)
            }
        }
    }
}