package com.app.quran.service.audio

import android.content.Context
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.app.quran.data.local.db.entity.PlayHistoryEntity
import com.app.quran.data.local.preferences.PreferencesManager
import com.app.quran.data.model.Surah
import com.app.quran.domain.repository.ISurahRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val surahRepository: ISurahRepository,
    private val preferencesManager: PreferencesManager
) {
    private var exoPlayer: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _sleepTimerState = MutableStateFlow(SleepTimerState())
    val sleepTimerState: StateFlow<SleepTimerState> = _sleepTimerState.asStateFlow()

    private val _repeatAyahState = MutableStateFlow(RepeatAyahState())
    val repeatAyahState: StateFlow<RepeatAyahState> = _repeatAyahState.asStateFlow()

    private var currentSurah: Surah? = null
    private var currentQueue: List<Surah> = emptyList()
    private var sleepTimerJob: Job? = null
    private var currentRepeatCount = 0

    fun initialize() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(playerListener)
            }
        }
    }

    fun playSurah(surah: Surah, startPositionMs: Long? = null) {
        initialize()
        currentSurah = surah

        val assetUri = "asset:///${surah.audioPath}"
        val mediaItem = MediaItem.fromUri(assetUri)

        exoPlayer?.apply {
            setMediaItem(mediaItem)
            prepare()
            if (startPositionMs != null && startPositionMs > 0) {
                seekTo(startPositionMs)
            }
            play()
        }

        _playerState.value = _playerState.value.copy(
            isPlaying = true,
            currentSurah = surah,
            isLoading = false,
            currentPositionMs = startPositionMs ?: 0L
        )
    }

    fun playRandomSurah() {
        scope.launch {
            _playerState.value = _playerState.value.copy(isLoading = true)

            val excludeId = currentSurah?.id
            val randomSurah = surahRepository.getRandomSurahExcluding(excludeId ?: -1)

            randomSurah?.let {
                playSurah(it)
            } ?: run {
                val surah = surahRepository.getSurahById(1)
                surah?.let { playSurah(it) }
            }
        }
    }

    fun play() {
        exoPlayer?.play()
        _playerState.value = _playerState.value.copy(isPlaying = true)
    }

    fun pause() {
        exoPlayer?.pause()
        _playerState.value = _playerState.value.copy(isPlaying = false)
    }

    fun stop() {
        exoPlayer?.stop()
        _playerState.value = _playerState.value.copy(
            isPlaying = false,
            currentSurah = null
        )
        currentSurah = null
        cancelSleepTimer()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _playerState.value = _playerState.value.copy(currentPositionMs = positionMs)
    }

    fun skipToNext() {
        if (preferencesManager.shuffleMode) {
            playRandomSurah()
        } else {
            playNextInQueue()
        }
    }

    fun skipToPrevious() {
        playRandomSurah()
    }

    private fun playNextInQueue() {
        currentSurah?.let { current ->
            val nextIndex = currentQueue.indexOf(current) + 1
            if (nextIndex < currentQueue.size) {
                playSurah(currentQueue[nextIndex])
            } else if (currentQueue.isNotEmpty()) {
                playSurah(currentQueue[0])
            } else {
                playRandomSurah()
            }
        } ?: playRandomSurah()
    }

    // Sleep Timer
    fun startSleepTimer(minutes: Int) {
        cancelSleepTimer()

        val endTime = System.currentTimeMillis() + (minutes * 60 * 1000L)

        _sleepTimerState.value = SleepTimerState(
            isActive = true,
            remainingMinutes = minutes,
            endTimeMs = endTime
        )

        sleepTimerJob = scope.launch {
            while (true) {
                val remaining = endTime - System.currentTimeMillis()
                if (remaining <= 0) {
                    // Fade out volume gradually
                    fadeOutAndStop()
                    break
                }

                val remainingMinutes = (remaining / 60000).toInt() + 1
                _sleepTimerState.value = _sleepTimerState.value.copy(
                    remainingMinutes = remainingMinutes
                )

                // Notify at 5 minutes remaining
                if (remaining <= 5 * 60 * 1000L && remaining > 4.9 * 60 * 1000L) {
                    // Could send notification here
                }

                delay(1000)
            }
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _sleepTimerState.value = SleepTimerState(isActive = false)
    }

    private suspend fun fadeOutAndStop() {
        exoPlayer?.let { player ->
            val startVolume = player.volume
            var currentVolume = startVolume

            while (currentVolume > 0) {
                currentVolume -= 0.1f
                player.volume = maxOf(0f, currentVolume)
                delay(500)
            }

            stop()
            player.volume = startVolume // Reset volume
        }
    }

    // Repeat Ayah
    fun enableRepeatAyah(repeatCount: Int = -1) { // -1 = infinite
        _repeatAyahState.value = RepeatAyahState(
            isEnabled = true,
            repeatCount = repeatCount,
            currentCount = 0
        )
        currentRepeatCount = 0
    }

    fun disableRepeatAyah() {
        _repeatAyahState.value = RepeatAyahState(isEnabled = false)
        currentRepeatCount = 0
    }

    fun setRepeatPosition(positionMs: Long) {
        _repeatAyahState.value = _repeatAyahState.value.copy(
            startPositionMs = positionMs
        )
    }

    private fun handleRepeatAyah() {
        val state = _repeatAyahState.value
        if (state.isEnabled && state.startPositionMs != null) {
            currentRepeatCount++
            if (state.repeatCount == -1 || currentRepeatCount < state.repeatCount) {
                exoPlayer?.seekTo(state.startPositionMs)
            } else {
                disableRepeatAyah()
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    _playerState.value = _playerState.value.copy(
                        isLoading = false,
                        duration = exoPlayer?.duration ?: 0L
                    )
                }
                Player.STATE_BUFFERING -> {
                    _playerState.value = _playerState.value.copy(isLoading = true)
                }
                Player.STATE_ENDED -> {
                    if (_repeatAyahState.value.isEnabled) {
                        handleRepeatAyah()
                    } else {
                        handleSurahEnded()
                    }
                }
                Player.STATE_IDLE -> {
                    _playerState.value = _playerState.value.copy(isLoading = false)
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.value = _playerState.value.copy(
                isPlaying = isPlaying,
                currentPositionMs = exoPlayer?.currentPosition ?: 0L
            )
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            _playerState.value = _playerState.value.copy(
                currentPositionMs = newPosition.positionMs
            )
        }
    }

    private fun handleSurahEnded() {
        if (preferencesManager.shuffleMode) {
            playRandomSurah()
        } else {
            playNextInQueue()
        }
    }

    fun updatePosition() {
        exoPlayer?.let {
            _playerState.value = _playerState.value.copy(
                currentPositionMs = it.currentPosition
            )
        }
    }

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = exoPlayer?.duration ?: 0L

    fun release() {
        scope.cancel()
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
    }
}

data class PlayerState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurah: Surah? = null,
    val duration: Long = 0L,
    val currentPositionMs: Long = 0L,
    val error: String? = null
)

data class SleepTimerState(
    val isActive: Boolean = false,
    val remainingMinutes: Int = 0,
    val endTimeMs: Long = 0L
)

data class RepeatAyahState(
    val isEnabled: Boolean = false,
    val startPositionMs: Long? = null,
    val repeatCount: Int = 0, // -1 = infinite
    val currentCount: Int = 0
)