package com.app.quran.data.model

data class Reader(
    val id: Int,
    val name: String,
    val nameArabic: String = "",
    val country: String,
    val description: String,
    val audioPathPrefix: String,
    val isDefault: Boolean
)