package com.example.techevents.domain.usecase

import com.example.techevents.data.repository.Result
import com.example.techevents.domain.repository.EventRepository

class DeleteEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteEvent(id)
}
