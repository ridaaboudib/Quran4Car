package com.app.quran.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.quran.data.local.dao.BookmarkDao
import com.app.quran.data.local.dao.ReaderDao
import com.app.quran.data.local.dao.SurahDao
import com.app.quran.data.local.entity.BookmarkEntity
import com.app.quran.data.local.entity.ReaderEntity
import com.app.quran.data.local.entity.SurahEntity

@Database(
    entities = [SurahEntity::class, ReaderEntity::class, BookmarkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun readerDao(): ReaderDao
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: QuranDatabase? = null

        fun getInstance(context: Context): QuranDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuranDatabase::class.java,
                    "quran_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
