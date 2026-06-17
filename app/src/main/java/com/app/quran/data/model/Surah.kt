package com.app.quran.data.model

data class Surah(
    val id: Int,
    val name: String,
    val nameArabic: String,
    val readerId: Int,
    val audioPath: String,
    val durationMs: Long,
    val type: SurahType,
    val versesCount: Int
)

enum class SurahType {
    MEACCAN,
    MEDINAN;

    companion object {
        fun fromString(value: String): SurahType {
            return when (value.lowercase()) {
                "meccan" -> MEACCAN
                "medinan" -> MEDINAN
                else -> MEACCAN
            }
        }
    }
}