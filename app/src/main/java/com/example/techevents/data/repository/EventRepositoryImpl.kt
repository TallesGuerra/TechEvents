package com.example.techevents.data.repository

import com.example.techevents.data.api.TechEventsApi
import com.example.techevents.data.dto.CreateEventRequest
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
                query = query.ifBlank { null },
                categories = category.ifBlank { null },
                onlineOnly = if (isOnline == true) true else null
            )
            Result.Success(response.events.map { it.toDomain() })
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

    override suspend fun createEvent(
        title: String,
        description: String,
        date: String,
        time: String,
        location: String,
        category: String,
        isOnline: Boolean,
        capacity: Int,
        link: String?
    ): Result<Event> = Result.Error(
        UnsupportedOperationException("Criação de eventos requer conta Eventbrite com organização")
    )

    private fun EventDto.toDomain(): Event {
        val dateParts = start.local?.split("T")
        val location = venue?.let {
            it.address?.localizedAddress ?: it.name ?: "Local não informado"
        } ?: if (onlineEvent) "Online" else "Local não informado"

        return Event(
            id = id,
            title = name.text ?: "",
            description = description?.text ?: "",
            date = dateParts?.getOrNull(0) ?: "",
            time = dateParts?.getOrNull(1)?.take(5) ?: "",
            location = location,
            category = "",
            isOnline = onlineEvent,
            capacity = capacity ?: 0,
            enrolled = 0,
            imageUrl = logo?.url,
            link = url
        )
    }
}
