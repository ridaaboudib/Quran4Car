package com.app.quran.presentation.readers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quran.data.model.Reader
import com.app.quran.domain.repository.IReaderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadersViewModel @Inject constructor(
    private val readerRepository: IReaderRepository
) : ViewModel() {

    private val _readers = MutableStateFlow<List<Reader>>(emptyList())
    val readers: StateFlow<List<Reader>> = _readers.asStateFlow()

    private val _selectedReader = MutableStateFlow<Reader?>(null)
    val selectedReader: StateFlow<Reader?> = _selectedReader.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadReaders()
    }

    private fun loadReaders() {
        viewModelScope.launch {
            readerRepository.getAllReaders().collect { readerList ->
                _readers.value = readerList
            }
        }
    }

    fun selectReader(reader: Reader) {
        _selectedReader.value = reader
    }

    fun setDefaultReader(reader: Reader) {
        viewModelScope.launch {
            readerRepository.setAsDefault(reader.id)
        }
    }

    fun addNewReader(
        name: String,
        country: String,
        description: String,
        audioPathPrefix: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val newReader = Reader(
                id = 0,
                name = name,
                country = country,
                description = description,
                audioPathPrefix = audioPathPrefix,
                isDefault = false
            )
            readerRepository.insertReader(newReader)
            _isLoading.value = false
        }
    }
}