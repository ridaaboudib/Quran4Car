package com.app.quran.presentation.settings

import androidx.lifecycle.ViewModel
import com.app.quran.data.local.preferences.PreferencesManager
import com.app.quran.service.bluetooth.BluetoothHelper
import com.app.quran.ui.theme.ThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _autoPlayEnabled = MutableStateFlow(preferencesManager.autoPlayEnabled)
    val autoPlayEnabled: StateFlow<Boolean> = _autoPlayEnabled.asStateFlow()

    private val _shuffleMode = MutableStateFlow(preferencesManager.shuffleMode)
    val shuffleMode: StateFlow<Boolean> = _shuffleMode.asStateFlow()

    private val _repeatMode = MutableStateFlow(preferencesManager.repeatMode)
    val repeatMode: StateFlow<Boolean> = _repeatMode.asStateFlow()

    private val _bluetoothAutoDetect = MutableStateFlow(preferencesManager.bluetoothAutoDetect)
    val bluetoothAutoDetect: StateFlow<Boolean> = _bluetoothAutoDetect.asStateFlow()

    private val _volume = MutableStateFlow(preferencesManager.volume)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _currentTheme = MutableStateFlow(ThemeType.LIGHT)
    val currentTheme: StateFlow<ThemeType> = _currentTheme.asStateFlow()

    private val _wifiOnlyDownload = MutableStateFlow(preferencesManager.wifiOnlyDownload)
    val wifiOnlyDownload: StateFlow<Boolean> = _wifiOnlyDownload.asStateFlow()

    private val _autoResume = MutableStateFlow(preferencesManager.autoResume)
    val autoResume: StateFlow<Boolean> = _autoResume.asStateFlow()

    val carKeywords: Set<String>
        get() = preferencesManager.carKeywords

    fun toggleAutoPlay() {
        val newValue = !preferencesManager.autoPlayEnabled
        preferencesManager.autoPlayEnabled = newValue
        _autoPlayEnabled.value = newValue
    }

    fun toggleShuffleMode() {
        val newValue = !preferencesManager.shuffleMode
        preferencesManager.shuffleMode = newValue
        _shuffleMode.value = newValue
    }

    fun toggleRepeatMode() {
        val newValue = !preferencesManager.repeatMode
        preferencesManager.repeatMode = newValue
        _repeatMode.value = newValue
    }

    fun toggleBluetoothAutoDetect() {
        val newValue = !preferencesManager.bluetoothAutoDetect
        preferencesManager.bluetoothAutoDetect = newValue
        _bluetoothAutoDetect.value = newValue
    }

    fun updateVolume(newVolume: Float) {
        preferencesManager.volume = newVolume
        _volume.value = newVolume
    }

    fun setTheme(theme: ThemeType) {
        preferencesManager.theme = theme
        _currentTheme.value = theme
    }

    fun toggleWifiOnlyDownload() {
        val newValue = !preferencesManager.wifiOnlyDownload
        preferencesManager.wifiOnlyDownload = newValue
        _wifiOnlyDownload.value = newValue
    }

    fun toggleAutoResume() {
        val newValue = !preferencesManager.autoResume
        preferencesManager.autoResume = newValue
        _autoResume.value = newValue
    }

    fun updateCarKeywords(keywords: Set<String>) {
        preferencesManager.carKeywords = keywords
    }
}