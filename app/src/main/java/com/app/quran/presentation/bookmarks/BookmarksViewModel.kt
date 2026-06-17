package com.app.quran.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quran.data.model.Bookmark
import com.app.quran.data.model.Surah
import com.app.quran.domain.repository.BookmarkRepository
import com.app.quran.domain.repository.ISurahRepository
import com.app.quran.service.audio.AudioPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val surahRepository: ISurahRepository,
    private val audioPlayerManager: AudioPlayerManager
) : ViewModel() {

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private val _surahCache = MutableStateFlow<Map<Int, Surah>>(emptyMap())
    val surahCache: StateFlow<Map<Int, Surah>> = _surahCache.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadBookmarks()
        loadSurahCache()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            bookmarkRepository.getAllBookmarks().collect { bookmarkList ->
                _bookmarks.value = bookmarkList
            }
        }
    }

    private fun loadSurahCache() {
        viewModelScope.launch {
            surahRepository.getAllSurahs().collect { surahs ->
                _surahCache.value = surahs.associateBy { it.id }
            }
        }
    }

    fun addBookmark(surahId: Int, positionMs: Long, note: String = "", readerId: Int = 1) {
        viewModelScope.launch {
            val bookmark = Bookmark(
                surahId = surahId,
                positionMs = positionMs,
                note = note,
                readerId = readerId
            )
            bookmarkRepository.insertBookmark(bookmark)
        }
    }

    fun updateBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkRepository.updateBookmark(bookmark)
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(bookmark)
        }
    }

    fun playFromBookmark(bookmark: Bookmark) {
        val surah = _surahCache.value[bookmark.surahId]
        surah?.let {
            audioPlayerManager.playSurah(it, bookmark.positionMs)
        }
    }

    fun getSurahForBookmark(bookmark: Bookmark): Surah? {
        return _surahCache.value[bookmark.surahId]
    }
}