package com.example.techevents.data.repository

import com.example.techevents.data.api.TechEventsApi
import com.example.techevents.data.dto.MeetupEventDto
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
            val response = api.getUpcomingEvents(text = query)
            val events = response.map { it.toDomain() }
            Result.Success(events)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getEventById(id: String): Result<Event> {
        return try {
            val dto = api.getEventDetail(id)
            Result.Success(dto.toDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun MeetupEventDto.toDomain(): Event {
        val venueName = venue?.name ?: group?.name ?: "Online"
        val venueAddress = venue?.address ?: ""
        val location = if (venueAddress.isNotBlank()) "$venueName - $venueAddress" else venueName

        return Event(
            id = id,
            title = name,
            description = description ?: "",
            date = localDate ?: "",
            time = localTime ?: "",
            location = location,
            category = group?.name ?: "",
            isOnline = isOnline,
            capacity = rsvpLimit ?: 0,
            enrolled = yesRsvpCount,
            imageUrl = featuredPhoto?.photoUrl
        )
    }
}
