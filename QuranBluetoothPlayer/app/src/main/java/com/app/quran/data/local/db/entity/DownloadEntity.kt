package com.app.quran.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val surahId: Int,
    val readerId: Int,
    val status: String = DownloadStatus.PENDING.name, // "PENDING", "DOWNLOADING", "COMPLETED", "FAILED", "PAUSED"
    val progress: Int = 0, // 0-100
    val localPath: String? = null,
    val fileSize: Long = 0L,
    val lastAttempt: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    PAUSED
}