package com.app.quran.data.local.db.dao

import androidx.room.*
import com.app.quran.data.local.db.entity.DownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    @Query("SELECT * FROM downloads WHERE readerId = :readerId")
    fun getDownloadsByReader(readerId: Int): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status = :status")
    fun getDownloadsByStatus(status: String): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE surahId = :surahId AND readerId = :readerId")
    suspend fun getDownload(surahId: Int, readerId: Int): DownloadEntity?

    @Query("SELECT * FROM downloads WHERE status = 'COMPLETED'")
    fun getCompletedDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status IN ('PENDING', 'DOWNLOADING', 'FAILED')")
    fun getPendingDownloads(): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloads(downloads: List<DownloadEntity>)

    @Update
    suspend fun updateDownload(download: DownloadEntity)

    @Delete
    suspend fun deleteDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE surahId = :surahId AND readerId = :readerId")
    suspend fun deleteDownload(surahId: Int, readerId: Int)

    @Query("UPDATE downloads SET status = :status, progress = :progress WHERE surahId = :surahId AND readerId = :readerId")
    suspend fun updateDownloadProgress(surahId: Int, readerId: Int, status: String, progress: Int)

    @Query("UPDATE downloads SET status = 'COMPLETED', localPath = :localPath, progress = 100 WHERE surahId = :surahId AND readerId = :readerId")
    suspend fun markDownloadCompleted(surahId: Int, readerId: Int, localPath: String)

    @Query("SELECT COUNT(*) FROM downloads WHERE status = 'COMPLETED' AND readerId = :readerId")
    suspend fun getCompletedCount(readerId: Int): Int

    @Query("SELECT SUM(fileSize) FROM downloads WHERE status = 'COMPLETED' AND readerId = :readerId")
    suspend fun getTotalDownloadedSize(readerId: Int): Long?
}