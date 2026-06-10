package com.app.quran.data.local.db.dao

import androidx.room.*
import com.app.quran.data.local.db.entity.PlayHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayHistoryDao {

    @Query("SELECT * FROM play_history ORDER BY playedAt DESC")
    fun getAllHistory(): Flow<List<PlayHistoryEntity>>

    @Query("SELECT * FROM play_history ORDER BY playedAt DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<PlayHistoryEntity>>

    @Query("SELECT * FROM play_history WHERE surahId = :surahId ORDER BY playedAt DESC")
    fun getHistoryBySurah(surahId: Int): Flow<List<PlayHistoryEntity>>

    @Query("SELECT * FROM play_history WHERE readerId = :readerId ORDER BY playedAt DESC")
    fun getHistoryByReader(readerId: Int): Flow<List<PlayHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: PlayHistoryEntity): Long

    @Update
    suspend fun updateHistory(history: PlayHistoryEntity)

    @Delete
    suspend fun deleteHistory(history: PlayHistoryEntity)

    @Query("DELETE FROM play_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)

    @Query("DELETE FROM play_history")
    suspend fun clearAllHistory()

    @Query("SELECT COUNT(*) FROM play_history WHERE surahId = :surahId")
    suspend fun getPlayCount(surahId: Int): Int

    @Query("SELECT * FROM play_history ORDER BY playedAt DESC LIMIT 1")
    suspend fun getLastPlayed(): PlayHistoryEntity?
}