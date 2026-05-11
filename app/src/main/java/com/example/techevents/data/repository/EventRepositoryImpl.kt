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
        page: Int,
        limit: Int,
        query: String,
        category: String,
        isOnline: Boolean?
    ): Result<List<Event>> {
        return try {
            val response = api.getEvents(
                page = page,
                limit = limit,
                search = query.ifBlank { null },
                category = category.ifBlank { null },
                isOnline = isOnline
            )
            val events = response.map { it.toDomain() }
            if (page == 1 && query.isBlank() && category.isBlank() && isOnline == null) {
                dao.clearAll()
                dao.insertAll(events.map { it.toEntity() })
            }
            Result.success(events)
        } catch (e: Exception) {
            if (page == 1 && query.isBlank() && category.isBlank() && isOnline == null) {
                val cached = dao.getAll()
                if (cached.isNotEmpty()) Result.success(cached.map { it.toDomain() })
                else Result.failure(e)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getEventById(id: String): Result<Event> {
        return try {
            Result.success(api.getEventById(id).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createEvent(
        title: String, description: String, date: String, time: String,
        location: String, category: String, isOnline: Boolean, capacity: Int, link: String?
    ): Result<Event> {
        return try {
            val request = CreateEventRequest(title, description, date, time, location, category, isOnline, capacity, link = link)
            Result.success(api.createEvent(request).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEvent(
        id: String, title: String, description: String, date: String, time: String,
        location: String, category: String, isOnline: Boolean, capacity: Int, link: String?
    ): Result<Event> {
        return try {
            val request = CreateEventRequest(title, description, date, time, location, category, isOnline, capacity, link = link)
            Result.success(api.updateEvent(id, request).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEvent(id: String): Result<Unit> {
        return try {
            api.deleteEvent(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun EventDto.toDomain() = Event(
        id = id, title = title, description = description, date = date, time = time,
        location = location, category = category, isOnline = isOnline, capacity = capacity,
        enrolled = enrolled, imageUrl = imageUrl, link = link
    )
}
