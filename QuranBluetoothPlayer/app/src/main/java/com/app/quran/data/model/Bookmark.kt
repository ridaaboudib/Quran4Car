package com.app.quran.data.model

data class Bookmark(
    val id: Int = 0,
    val surahId: Int,
    val positionMs: Long,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val readerId: Int
)