package com.example.techevents.data.repository

import com.example.techevents.data.api.TechEventsApi
import com.example.techevents.data.dto.EventDto
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.repository.EventRepository

class EventRepositoryImpl(private val api: TechEventsApi) : EventRepository {

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
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getEventById(id: String): Result<Event> {
        return try {
            Result.Success(api.getEventById(id).toDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun EventDto.toDomain() = Event(
        id = id,
        title = title,
        description = description,
        date = date,
        time = time,
        location = location,
        category = category,
        isOnline = isOnline,
        capacity = capacity,
        enrolled = enrolled,
        imageUrl = imageUrl
    )
}
