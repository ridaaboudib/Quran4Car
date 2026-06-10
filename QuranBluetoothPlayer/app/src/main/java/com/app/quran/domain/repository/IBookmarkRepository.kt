package com.app.quran.domain.repository

import com.app.quran.data.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface IBookmarkRepository {
    fun getAllBookmarks(): Flow<List<Bookmark>>
    fun getBookmarksBySurah(surahId: Int): Flow<List<Bookmark>>
    fun getBookmarksByReader(readerId: Int): Flow<List<Bookmark>>
    suspend fun getBookmarkById(id: Int): Bookmark?
    suspend fun insertBookmark(bookmark: Bookmark): Long
    suspend fun updateBookmark(bookmark: Bookmark)
    suspend fun deleteBookmark(bookmark: Bookmark)
    suspend fun deleteBookmarkById(id: Int)
    suspend fun getBookmarkCount(): Int
}