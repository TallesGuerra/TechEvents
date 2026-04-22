package com.example.techevents.domain.usecase

import com.example.techevents.data.repository.Result
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.repository.EventRepository

class UpdateEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(
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
    ): Result<Event> = repository.updateEvent(
        id, title, description, date, time, location, category, isOnline, capacity, link
    )
}
