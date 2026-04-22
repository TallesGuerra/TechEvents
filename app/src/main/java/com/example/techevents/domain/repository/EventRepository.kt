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

    suspend fun createEvent(
        title: String,
        description: String,
        date: String,
        time: String,
        location: String,
        category: String,
        isOnline: Boolean,
        capacity: Int,
        link: String?
    ): Result<Event>

    suspend fun updateEvent(
        id: String,
        title: String,
        description: String,
        date: String,
        time: String,
        location: String,
        category: String,
        isOnline: Boolean,
        capacity: Int,
        link: String?
    ): Result<Event>

    suspend fun deleteEvent(id: String): Result<Unit>
}
