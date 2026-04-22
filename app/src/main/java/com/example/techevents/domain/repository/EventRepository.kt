package com.example.techevents.domain.repository

import com.example.techevents.data.repository.Result
import com.example.techevents.domain.model.Event

interface EventRepository {
    suspend fun getEvents(
        page: Int,
        limit: Int,
        query: String = "",
        category: String = "",
        isOnline: Boolean? = null
    ): Result<List<Event>>

    suspend fun getEventById(id: String): Result<Event>
}
