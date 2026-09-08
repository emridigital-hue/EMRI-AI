package com.example.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun insert(item: HistoryEntity): Long = historyDao.insert(item)

    suspend fun deleteById(id: Long) = historyDao.deleteById(id)

    suspend fun clearAll() = historyDao.clearAll()
}
