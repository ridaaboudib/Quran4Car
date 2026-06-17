package com.app.quran.di

import android.content.Context
import com.app.quran.data.local.dao.BookmarkDao
import com.app.quran.data.local.dao.ReaderDao
import com.app.quran.data.local.dao.SurahDao
import com.app.quran.data.local.database.QuranDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideQuranDatabase(
        @ApplicationContext context: Context
    ): QuranDatabase = QuranDatabase.getInstance(context)

    @Provides
    fun provideSurahDao(database: QuranDatabase): SurahDao = database.surahDao()

    @Provides
    fun provideReaderDao(database: QuranDatabase): ReaderDao = database.readerDao()

    @Provides
    fun provideBookmarkDao(database: QuranDatabase): BookmarkDao = database.bookmarkDao()
}
