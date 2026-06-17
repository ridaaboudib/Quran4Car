package com.app.quran.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.quran.data.local.entity.ReaderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReaderDao {
    @Insert
    suspend fun insert(reader: ReaderEntity): Long

    @Query("SELECT * FROM readers")
    fun getAllReaders(): Flow<List<ReaderEntity>>

    @Query("SELECT * FROM readers WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultReader(): ReaderEntity?

    @Query("UPDATE readers SET isDefault = 0 WHERE isDefault = 1")
    suspend fun clearDefaultReader()

    @Query("UPDATE readers SET isDefault = 1 WHERE id = :readerId")
    suspend fun setDefaultReader(readerId: Int)
}
