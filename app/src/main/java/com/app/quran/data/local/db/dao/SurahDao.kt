package com.app.quran.data.local.db.dao

import androidx.room.*
import com.app.quran.data.local.db.entity.SurahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {

    @Query("SELECT * FROM surahs ORDER BY id ASC")
    fun getAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE readerId = :readerId ORDER BY id ASC")
    fun getSurahsByReader(readerId: Int): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE id = :id")
    suspend fun getSurahById(id: Int): SurahEntity?

    @Query("SELECT * FROM surahs WHERE id != :excludeId ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomSurahExcluding(excludeId: Int): SurahEntity?

    @Query("SELECT * FROM surahs WHERE nameArabic LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchSurahs(query: String): Flow<List<SurahEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurah(surah: SurahEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    @Update
    suspend fun updateSurah(surah: SurahEntity)

    @Delete
    suspend fun deleteSurah(surah: SurahEntity)

    @Query("DELETE FROM surahs WHERE readerId = :readerId")
    suspend fun deleteSurahsByReader(readerId: Int)

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahCount(): Int
}