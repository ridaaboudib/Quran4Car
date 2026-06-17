package com.app.quran.domain.repository

import com.app.quran.data.model.Surah
import kotlinx.coroutines.flow.Flow

interface ISurahRepository {
    fun getAllSurahs(): Flow<List<Surah>>
    fun getSurahsByReader(readerId: Int): Flow<List<Surah>>
    suspend fun getSurahById(id: Int): Surah?
    suspend fun getRandomSurahExcluding(excludeId: Int): Surah?
    fun searchSurahs(query: String): Flow<List<Surah>>
    suspend fun insertSurah(surah: Surah)
    suspend fun updateSurah(surah: Surah)
    suspend fun deleteSurah(surah: Surah)
    suspend fun getSurahCount(): Int
}