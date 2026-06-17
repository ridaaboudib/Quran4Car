package com.app.quran.domain.repository

import com.app.quran.data.model.Reader
import kotlinx.coroutines.flow.Flow

interface IReaderRepository {
    fun getAllReaders(): Flow<List<Reader>>
    suspend fun getReaderById(id: Int): Reader?
    suspend fun getDefaultReader(): Reader?
    fun getDefaultReaderFlow(): Flow<Reader?>
    suspend fun insertReader(reader: Reader): Long
    suspend fun updateReader(reader: Reader)
    suspend fun deleteReader(reader: Reader)
    suspend fun setAsDefault(readerId: Int)
}