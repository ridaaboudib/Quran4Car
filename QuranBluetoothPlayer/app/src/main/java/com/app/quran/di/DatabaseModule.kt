package com.app.quran.di

import android.content.Context
import com.app.quran.data.local.db.AppDatabase
import com.app.quran.data.local.db.dao.ReaderDao
import com.app.quran.data.local.db.dao.SurahDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideSurahDao(database: AppDatabase): SurahDao {
        return database.surahDao()
    }

    @Provides
    @Singleton
    fun provideReaderDao(database: AppDatabase): ReaderDao {
        return database.readerDao()
    }
}