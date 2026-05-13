package com.example.techevents.data.datasource

import com.example.techevents.data.local.EventDao
import com.example.techevents.data.local.EventEntity

class LocalDataSourceImpl(private val dao: EventDao) : LocalDataSource {
    override suspend fun getAll() = dao.getAll()
    override suspend fun insertAll(events: List<EventEntity>) = dao.insertAll(events)
    override suspend fun clearAll() = dao.clearAll()
}