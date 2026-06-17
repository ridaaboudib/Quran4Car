package com.app.quran.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahId: Int,
    val positionMs: Long,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val readerId: Int
)