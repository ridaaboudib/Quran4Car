package com.app.quran.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_history")
data class PlayHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahId: Int,
    val readerId: Int,
    val playedAt: Long = System.currentTimeMillis(),
    val durationListened: Long = 0L,
    val completed: Boolean = false
)