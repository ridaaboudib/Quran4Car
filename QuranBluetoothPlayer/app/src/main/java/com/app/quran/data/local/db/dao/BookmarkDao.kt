package com.app.quran.data.local.db.dao

import androidx.room.*
import com.app.quran.data.local.db.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE surahId = :surahId ORDER BY positionMs ASC")
    fun getBookmarksBySurah(surahId: Int): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE readerId = :readerId ORDER BY createdAt DESC")
    fun getBookmarksByReader(readerId: Int): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: Int): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Update
    suspend fun updateBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Int)

    @Query("DELETE FROM bookmarks WHERE surahId = :surahId")
    suspend fun deleteBookmarksBySurah(surahId: Int)

    @Query("SELECT COUNT(*) FROM bookmarks")
    suspend fun getBookmarkCount(): Int
}