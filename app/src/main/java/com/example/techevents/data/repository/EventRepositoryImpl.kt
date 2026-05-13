package com.example.techevents.data.repository

import com.example.techevents.data.api.TechEventsApi
import com.example.techevents.data.dto.CreateEventRequest
import com.example.techevents.data.dto.EventDto
import com.example.techevents.data.local.EventDao
import com.example.techevents.data.local.toDomain
import com.example.techevents.data.local.toEntity
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.repository.EventRepository

class EventRepositoryImpl(
    private val api: TechEventsApi,
    private val dao: EventDao
) : EventRepository {

    override suspend fun getEvents(
        page: Int, limit: Int, query: String, category: String, isOnline: Boolean?
    ): Result<List<Event>> {
        return runCatching {
            val events = api.getEvents(
                page = page,
                limit = limit,
                search = query.ifBlank { null },
                category = category.ifBlank { null },
                isOnline = isOnline
            ).map { it.toDomain() }

            if (page == 1 && query.isBlank() && category.isBlank() && isOnline == null) {
                dao.clearAll()
                dao.insertAll(events.map { it.toEntity() })
            }
            events
        }.recoverCatching { e ->
            if (page == 1 && query.isBlank() && category.isBlank() && isOnline == null) {
                val cached = dao.getAll()
                if (cached.isNotEmpty()) cached.map { it.toDomain() }
                else throw e
            } else throw e
        }
    }

    override suspend fun getEventById(id: String): Result<Event> =
        runCatching { api.getEventById(id).toDomain() }

    override suspend fun createEvent(
        title: String, description: String, date: String, time: String,
        location: String, category: String, isOnline: Boolean, capacity: Int, link: String?
    ): Result<Event> = runCatching {
        val request = CreateEventRequest(title, description, date, time, location, category, isOnline, capacity, link = link)
        api.createEvent(request).toDomain()
    }

    override suspend fun updateEvent(
        id: String, title: String, description: String, date: String, time: String,
        location: String, category: String, isOnline: Boolean, capacity: Int, link: String?
    ): Result<Event> = runCatching {
        val request = CreateEventRequest(title, description, date, time, location, category, isOnline, capacity, link = link)
        api.updateEvent(id, request).toDomain()
    }

    override suspend fun deleteEvent(id: String): Result<Unit> =
        runCatching { api.deleteEvent(id); Unit }

    private fun EventDto.toDomain() = Event(
        id = id, title = title, description = description, date = date, time = time,
        location = location, category = category, isOnline = isOnline, capacity = capacity,
        enrolled = enrolled, imageUrl = imageUrl, link = link
    )
}