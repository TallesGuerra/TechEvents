package com.example.techevents.domain.usecase

import com.example.techevents.data.repository.Result
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.repository.EventRepository

class GetEventDetailUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(id: String): Result<Event> = repository.getEventById(id)
}
