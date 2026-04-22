package com.example.techevents.domain.usecase

import com.example.techevents.data.repository.Result
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.repository.EventRepository

class GetEventsUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 20,
        query: String = "",
        category: String = "",
        isOnline: Boolean? = null
    ): Result<List<Event>> = repository.getEvents(page, limit, query, category, isOnline)
}
