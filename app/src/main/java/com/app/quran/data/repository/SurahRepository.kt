package com.app.quran.data.repository

import com.app.quran.data.local.db.dao.SurahDao
import com.app.quran.data.local.db.entity.SurahEntity
import com.app.quran.data.model.Surah
import com.app.quran.data.model.SurahType
import com.app.quran.domain.repository.ISurahRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurahRepository @Inject constructor(
    private val surahDao: SurahDao
) : ISurahRepository {

    override fun getAllSurahs(): Flow<List<Surah>> {
        return surahDao.getAllSurahs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSurahsByReader(readerId: Int): Flow<List<Surah>> {
        return surahDao.getSurahsByReader(readerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSurahById(id: Int): Surah? {
        return surahDao.getSurahById(id)?.toDomain()
    }

    override suspend fun getRandomSurahExcluding(excludeId: Int): Surah? {
        return surahDao.getRandomSurahExcluding(excludeId)?.toDomain()
    }

    override fun searchSurahs(query: String): Flow<List<Surah>> {
        return surahDao.searchSurahs(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSurah(surah: Surah) {
        surahDao.insertSurah(surah.toEntity())
    }

    override suspend fun updateSurah(surah: Surah) {
        surahDao.updateSurah(surah.toEntity())
    }

    override suspend fun deleteSurah(surah: Surah) {
        surahDao.deleteSurah(surah.toEntity())
    }

    override suspend fun getSurahCount(): Int {
        return surahDao.getSurahCount()
    }

    private fun SurahEntity.toDomain(): Surah {
        return Surah(
            id = id,
            name = name,
            nameArabic = nameArabic,
            readerId = readerId,
            audioPath = audioPath,
            durationMs = durationMs,
            type = SurahType.fromString(type),
            versesCount = versesCount
        )
    }

    private fun Surah.toEntity(): SurahEntity {
        return SurahEntity(
            id = id,
            name = name,
            nameArabic = nameArabic,
            readerId = readerId,
            audioPath = audioPath,
            durationMs = durationMs,
            type = when (type) {
                SurahType.MEACCAN -> "meccan"
                SurahType.MEDINAN -> "medinan"
            },
            versesCount = versesCount
        )
    }
}