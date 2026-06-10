package com.app.quran.domain.usecase

import com.app.quran.data.model.Surah
import com.app.quran.domain.repository.ISurahRepository
import javax.inject.Inject

class PlaySurahUseCase @Inject constructor(
    private val surahRepository: ISurahRepository
) {
    suspend fun getSurahById(id: Int): Surah? {
        return surahRepository.getSurahById(id)
    }
}