package com.example.techevents.data.datasource

import com.example.techevents.data.local.EventEntity

interface LocalDataSource {
    suspend fun getAll(): List<EventEntity>
    suspend fun insertAll(events: List<EventEntity>)
    suspend fun clearAll()
}