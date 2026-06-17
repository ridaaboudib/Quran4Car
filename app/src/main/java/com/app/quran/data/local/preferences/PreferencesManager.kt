package com.app.quran.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.app.quran.service.bluetooth.BluetoothHelper
import com.app.quran.ui.theme.ThemeType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "quran_bluetooth_prefs"
        private const val KEY_AUTO_PLAY = "auto_play_enabled"
        private const val KEY_SHUFFLE_MODE = "shuffle_mode"
        private const val KEY_REPEAT_MODE = "repeat_mode"
        private const val KEY_LAST_READER_ID = "last_reader_id"
        private const val KEY_LAST_SURAH_ID = "last_surah_id"
        private const val KEY_BLUETOOTH_AUTO_DETECT = "bluetooth_auto_detect"
        private const val KEY_CAR_KEYWORDS = "car_keywords"
        private const val KEY_VOLUME = "volume"
        private const val KEY_THEME = "theme"
        private const val KEY_WIFI_ONLY_DOWNLOAD = "wifi_only_download"
        private const val KEY_AUTO_RESUME = "auto_resume"
    }

    var autoPlayEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_PLAY, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_PLAY, value).apply()

    var shuffleMode: Boolean
        get() = prefs.getBoolean(KEY_SHUFFLE_MODE, true)
        set(value) = prefs.edit().putBoolean(KEY_SHUFFLE_MODE, value).apply()

    var repeatMode: Boolean
        get() = prefs.getBoolean(KEY_REPEAT_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_REPEAT_MODE, value).apply()

    var lastReaderId: Int
        get() = prefs.getInt(KEY_LAST_READER_ID, 1)
        set(value) = prefs.edit().putInt(KEY_LAST_READER_ID, value).apply()

    var lastSurahId: Int
        get() = prefs.getInt(KEY_LAST_SURAH_ID, 1)
        set(value) = prefs.edit().putInt(KEY_LAST_SURAH_ID, value).apply()

    var bluetoothAutoDetect: Boolean
        get() = prefs.getBoolean(KEY_BLUETOOTH_AUTO_DETECT, true)
        set(value) = prefs.edit().putBoolean(KEY_BLUETOOTH_AUTO_DETECT, value).apply()

    var carKeywords: Set<String>
        get() = prefs.getStringSet(KEY_CAR_KEYWORDS, DEFAULT_CAR_KEYWORDS) ?: DEFAULT_CAR_KEYWORDS
        set(value) = prefs.edit().putStringSet(KEY_CAR_KEYWORDS, value).apply()

    var volume: Float
        get() = prefs.getFloat(KEY_VOLUME, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_VOLUME, value).apply()

    var theme: ThemeType
        get() {
            val themeName = prefs.getString(KEY_THEME, ThemeType.LIGHT.name) ?: ThemeType.LIGHT.name
            return try {
                ThemeType.valueOf(themeName)
            } catch (e: Exception) {
                ThemeType.LIGHT
            }
        }
        set(value) = prefs.edit().putString(KEY_THEME, value.name).apply()

    var wifiOnlyDownload: Boolean
        get() = prefs.getBoolean(KEY_WIFI_ONLY_DOWNLOAD, true)
        set(value) = prefs.edit().putBoolean(KEY_WIFI_ONLY_DOWNLOAD, value).apply()

    var autoResume: Boolean
        get() = prefs.getBoolean(KEY_AUTO_RESUME, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_RESUME, value).apply()

    companion object {
        val DEFAULT_CAR_KEYWORDS = BluetoothHelper.CAR_KEYWORDS.toSet()
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }

    fun exportSettings(): Map<String, Any> {
        return mapOf(
            "autoPlayEnabled" to autoPlayEnabled,
            "shuffleMode" to shuffleMode,
            "repeatMode" to repeatMode,
            "bluetoothAutoDetect" to bluetoothAutoDetect,
            "volume" to volume,
            "theme" to theme.name,
            "wifiOnlyDownload" to wifiOnlyDownload,
            "autoResume" to autoResume,
            "carKeywords" to carKeywords.toList()
        )
    }

    fun importSettings(settings: Map<String, Any>) {
        settings["autoPlayEnabled"]?.let { autoPlayEnabled = it as Boolean }
        settings["shuffleMode"]?.let { shuffleMode = it as Boolean }
        settings["repeatMode"]?.let { repeatMode = it as Boolean }
        settings["bluetoothAutoDetect"]?.let { bluetoothAutoDetect = it as Boolean }
        settings["volume"]?.let { volume = (it as Number).toFloat() }
        settings["theme"]?.let {
            try {
                theme = ThemeType.valueOf(it as String)
            } catch (e: Exception) { }
        }
        settings["wifiOnlyDownload"]?.let { wifiOnlyDownload = it as Boolean }
        settings["autoResume"]?.let { autoResume = it as Boolean }
        @Suppress("UNCHECKED_CAST")
        (settings["carKeywords"] as? List<String>)?.let { carKeywords = it.toSet() }
    }
}