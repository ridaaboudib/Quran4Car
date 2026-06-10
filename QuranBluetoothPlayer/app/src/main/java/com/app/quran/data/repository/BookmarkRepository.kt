package com.app.quran.data.repository

import com.app.quran.data.local.db.dao.BookmarkDao
import com.app.quran.data.local.db.entity.BookmarkEntity
import com.app.quran.data.model.Bookmark
import com.app.quran.domain.repository.IBookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : IBookmarkRepository {

    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBookmarksBySurah(surahId: Int): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksBySurah(surahId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBookmarksByReader(readerId: Int): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksByReader(readerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getBookmarkById(id: Int): Bookmark? {
        return bookmarkDao.getBookmarkById(id)?.toDomain()
    }

    override suspend fun insertBookmark(bookmark: Bookmark): Long {
        return bookmarkDao.insertBookmark(bookmark.toEntity())
    }

    override suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.updateBookmark(bookmark.toEntity())
    }

    override suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.deleteBookmark(bookmark.toEntity())
    }

    override suspend fun deleteBookmarkById(id: Int) {
        bookmarkDao.deleteBookmarkById(id)
    }

    override suspend fun getBookmarkCount(): Int {
        return bookmarkDao.getBookmarkCount()
    }

    private fun BookmarkEntity.toDomain(): Bookmark {
        return Bookmark(
            id = id,
            surahId = surahId,
            positionMs = positionMs,
            note = note,
            createdAt = createdAt,
            readerId = readerId
        )
    }

    private fun Bookmark.toEntity(): BookmarkEntity {
        return BookmarkEntity(
            id = id,
            surahId = surahId,
            positionMs = positionMs,
            note = note,
            createdAt = createdAt,
            readerId = readerId
        )
    }
}