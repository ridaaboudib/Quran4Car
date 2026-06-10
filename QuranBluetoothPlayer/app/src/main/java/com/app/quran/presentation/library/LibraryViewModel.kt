package com.app.quran.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quran.data.model.Surah
import com.app.quran.domain.repository.ISurahRepository
import com.app.quran.service.audio.AudioPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val surahRepository: ISurahRepository,
    private val audioPlayerManager: AudioPlayerManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _surahs = MutableStateFlow<List<Surah>>(emptyList())
    val surahs: StateFlow<List<Surah>> = _surahs.asStateFlow()

    private val _filteredSurahs = MutableStateFlow<List<Surah>>(emptyList())
    val filteredSurahs: StateFlow<List<Surah>> = _filteredSurahs.asStateFlow()

    private val _selectedSurah = MutableStateFlow<Surah?>(null)
    val selectedSurah: StateFlow<Surah?> = _selectedSurah.asStateFlow()

    init {
        loadSurahs()
        setupSearch()
    }

    private fun loadSurahs() {
        viewModelScope.launch {
            surahRepository.getAllSurahs().collect { surahList ->
                _surahs.value = surahList
                _filteredSurahs.value = surahList
            }
        }
    }

    private fun setupSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _filteredSurahs.value = _surahs.value
                    } else {
                        _filteredSurahs.value = _surahs.value.filter { surah ->
                            surah.nameArabic.contains(query, ignoreCase = true) ||
                            surah.name.contains(query, ignoreCase = true)
                        }
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectSurah(surah: Surah) {
        _selectedSurah.value = surah
    }

    fun playSurah(surah: Surah) {
        audioPlayerManager.playSurah(surah)
    }

    fun playRandomSurah() {
        audioPlayerManager.playRandomSurah()
    }
}