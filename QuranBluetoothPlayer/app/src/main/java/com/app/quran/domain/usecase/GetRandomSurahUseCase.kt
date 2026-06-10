package com.app.quran.domain.usecase

import com.app.quran.data.model.Surah
import com.app.quran.domain.repository.ISurahRepository
import javax.inject.Inject

class GetRandomSurahUseCase @Inject constructor(
    private val surahRepository: ISurahRepository
) {
    suspend operator fun invoke(excludeId: Int? = null): Surah? {
        return if (excludeId != null) {
            surahRepository.getRandomSurahExcluding(excludeId)
        } else {
            surahRepository.getRandomSurahExcluding(-1)
        }
    }
}