package com.app.quran.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val nameArabic: String,
    val readerId: Int,
    val audioPath: String,
    val durationMs: Long,
    val type: String, // "meccan" or "medinan"
    val versesCount: Int
)