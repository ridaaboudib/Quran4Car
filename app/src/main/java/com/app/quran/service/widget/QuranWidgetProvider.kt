package com.app.quran.service.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.app.quran.R

class QuranWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        appWidgetIds?.forEach { appWidgetId ->
            val views = RemoteViews(context?.packageName, R.layout.widget_layout)
            
            views.setTextViewText(R.id.widget_surah_name, "سورة الفاتحة")
            views.setProgressBar(R.id.widget_progress, 100, 50, false)
            
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        when (intent?.action) {
            "com.app.quran.widget.ACTION_PLAY_PAUSE" -> {
            }
            "com.app.quran.widget.ACTION_NEXT" -> {
            }
        }
    }
}
