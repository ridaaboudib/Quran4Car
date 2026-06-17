package com.app.quran.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.quran.data.local.entity.SurahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Insert
    suspend fun insertAll(surahs: List<SurahEntity>)

    @Query("SELECT * FROM surahs ORDER BY id")
    fun getAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE id = :surahId")
    suspend fun getSurahById(surahId: Int): SurahEntity?

    @Query("SELECT * FROM surahs WHERE type = :type ORDER BY id")
    fun getSurahsByType(type: String): Flow<List<SurahEntity>>
}
