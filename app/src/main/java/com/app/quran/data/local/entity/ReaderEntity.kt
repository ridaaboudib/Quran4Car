package com.app.quran.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "readers")
data class ReaderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val country: String,
    val description: String,
    val audioPathPrefix: String,
    val isDefault: Boolean = false
)
