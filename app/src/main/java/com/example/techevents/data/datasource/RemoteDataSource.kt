package com.example.techevents.data.datasource

import com.example.techevents.data.dto.CreateEventRequest
import com.example.techevents.data.dto.EventDto

interface RemoteDataSource {
    suspend fun getEvents(page: Int, limit: Int, search: String?, category: String?, isOnline: Boolean?): List<EventDto>
    suspend fun getEventById(id: String): EventDto
    suspend fun createEvent(request: CreateEventRequest): EventDto
    suspend fun updateEvent(id: String, request: CreateEventRequest): EventDto
    suspend fun deleteEvent(id: String)
}