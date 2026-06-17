package com.app.quran.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quran.data.local.preferences.PreferencesManager
import com.app.quran.data.model.Bookmark
import com.app.quran.data.model.Reader
import com.app.quran.data.model.Surah
import com.app.quran.domain.repository.BookmarkRepository
import com.app.quran.domain.repository.IReaderRepository
import com.app.quran.domain.repository.ISurahRepository
import com.app.quran.service.audio.AudioPlayerManager
import com.app.quran.service.audio.PlayerState
import com.app.quran.service.audio.RepeatAyahState
import com.app.quran.service.audio.SleepTimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val surahRepository: ISurahRepository,
    private val readerRepository: IReaderRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val audioPlayerManager: AudioPlayerManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = audioPlayerManager.playerState

    val sleepTimerState: StateFlow<SleepTimerState> = audioPlayerManager.sleepTimerState

    val repeatAyahState: StateFlow<RepeatAyahState> = audioPlayerManager.repeatAyahState

    private val _currentReader = MutableStateFlow<Reader?>(null)
    val currentReader: StateFlow<Reader?> = _currentReader.asStateFlow()

    val shuffleMode: StateFlow<Boolean> = MutableStateFlow(preferencesManager.shuffleMode)

    init {
        viewModelScope.launch {
            readerRepository.getDefaultReaderFlow().collect { reader ->
                _currentReader.value = reader
            }
        }
    }

    fun playSurah(surah: Surah, startPositionMs: Long? = null) {
        audioPlayerManager.playSurah(surah, startPositionMs)
    }

    fun playRandomSurah() {
        audioPlayerManager.playRandomSurah()
    }

    fun play() {
        audioPlayerManager.play()
    }

    fun pause() {
        audioPlayerManager.pause()
    }

    fun skipToNext() {
        audioPlayerManager.skipToNext()
    }

    fun skipToPrevious() {
        audioPlayerManager.skipToPrevious()
    }

    fun toggleShuffle() {
        val newValue = !preferencesManager.shuffleMode
        preferencesManager.shuffleMode = newValue
        (shuffleMode as MutableStateFlow).value = newValue
    }

    fun toggleRepeat() {
        preferencesManager.repeatMode = !preferencesManager.repeatMode
    }

    fun seekTo(positionMs: Long) {
        audioPlayerManager.seekTo(positionMs)
    }

    // Sleep Timer
    fun startSleepTimer(minutes: Int) {
        audioPlayerManager.startSleepTimer(minutes)
    }

    fun cancelSleepTimer() {
        audioPlayerManager.cancelSleepTimer()
    }

    // Repeat Ayah
    fun enableRepeatAyah(repeatCount: Int = -1) {
        val currentPosition = audioPlayerManager.getCurrentPosition()
        audioPlayerManager.setRepeatPosition(currentPosition)
        audioPlayerManager.enableRepeatAyah(repeatCount)
    }

    fun disableRepeatAyah() {
        audioPlayerManager.disableRepeatAyah()
    }

    // Bookmarks
    fun addBookmark(surahId: Int, positionMs: Long, note: String = "") {
        viewModelScope.launch {
            val readerId = _currentReader.value?.id ?: 1
            val bookmark = Bookmark(
                surahId = surahId,
                positionMs = positionMs,
                note = note,
                readerId = readerId
            )
            bookmarkRepository.insertBookmark(bookmark)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Don't release here - service handles it
    }
}