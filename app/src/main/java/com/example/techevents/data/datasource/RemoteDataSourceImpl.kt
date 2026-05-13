package com.example.techevents.data.datasource

import com.example.techevents.data.api.TechEventsApi
import com.example.techevents.data.dto.CreateEventRequest
import com.example.techevents.data.dto.EventDto

class RemoteDataSourceImpl(private val api: TechEventsApi) : RemoteDataSource {
    override suspend fun getEvents(page: Int, limit: Int, search: String?, category: String?, isOnline: Boolean?) =
        api.getEvents(page, limit, search, category, isOnline)
    override suspend fun getEventById(id: String) = api.getEventById(id)
    override suspend fun createEvent(request: CreateEventRequest) = api.createEvent(request)
    override suspend fun updateEvent(id: String, request: CreateEventRequest) = api.updateEvent(id, request)
    override suspend fun deleteEvent(id: String) { api.deleteEvent(id) }
}