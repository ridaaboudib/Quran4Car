package com.app.quran.data.local.db.dao

import androidx.room.*
import com.app.quran.data.local.db.entity.ReaderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReaderDao {

    @Query("SELECT * FROM readers ORDER BY name ASC")
    fun getAllReaders(): Flow<List<ReaderEntity>>

    @Query("SELECT * FROM readers WHERE id = :id")
    suspend fun getReaderById(id: Int): ReaderEntity?

    @Query("SELECT * FROM readers WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultReader(): ReaderEntity?

    @Query("SELECT * FROM readers WHERE isDefault = 1 LIMIT 1")
    fun getDefaultReaderFlow(): Flow<ReaderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReader(reader: ReaderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaders(readers: List<ReaderEntity>)

    @Update
    suspend fun updateReader(reader: ReaderEntity)

    @Delete
    suspend fun deleteReader(reader: ReaderEntity)

    @Query("UPDATE readers SET isDefault = 0")
    suspend fun clearDefaultReader()

    @Query("UPDATE readers SET isDefault = 1 WHERE id = :readerId")
    suspend fun setDefaultReader(readerId: Int)

    @Transaction
    suspend fun setAsDefault(readerId: Int) {
        clearDefaultReader()
        setDefaultReader(readerId)
    }
}