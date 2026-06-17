package com.app.quran.data.repository

import com.app.quran.data.local.db.dao.ReaderDao
import com.app.quran.data.local.db.entity.ReaderEntity
import com.app.quran.data.model.Reader
import com.app.quran.domain.repository.IReaderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReaderRepository @Inject constructor(
    private val readerDao: ReaderDao
) : IReaderRepository {

    override fun getAllReaders(): Flow<List<Reader>> {
        return readerDao.getAllReaders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getReaderById(id: Int): Reader? {
        return readerDao.getReaderById(id)?.toDomain()
    }

    override suspend fun getDefaultReader(): Reader? {
        return readerDao.getDefaultReader()?.toDomain()
    }

    override fun getDefaultReaderFlow(): Flow<Reader?> {
        return readerDao.getDefaultReaderFlow().map { it?.toDomain() }
    }

    override suspend fun insertReader(reader: Reader): Long {
        return readerDao.insertReader(reader.toEntity())
    }

    override suspend fun updateReader(reader: Reader) {
        readerDao.updateReader(reader.toEntity())
    }

    override suspend fun deleteReader(reader: Reader) {
        readerDao.deleteReader(reader.toEntity())
    }

    override suspend fun setAsDefault(readerId: Int) {
        readerDao.setAsDefault(readerId)
    }

    private fun ReaderEntity.toDomain(): Reader {
        return Reader(
            id = id,
            name = name,
            country = country,
            description = description,
            audioPathPrefix = audioPathPrefix,
            isDefault = isDefault
        )
    }

    private fun Reader.toEntity(): ReaderEntity {
        return ReaderEntity(
            id = id,
            name = name,
            country = country,
            description = description,
            audioPathPrefix = audioPathPrefix,
            isDefault = isDefault
        )
    }
}