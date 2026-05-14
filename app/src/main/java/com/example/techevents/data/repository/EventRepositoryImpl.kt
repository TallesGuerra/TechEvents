package com.example.techevents.data.repository

import com.example.techevents.data.datasource.LocalDataSource
import com.example.techevents.data.datasource.RemoteDataSource
import com.example.techevents.data.dto.CreateEventRequest
import com.example.techevents.data.dto.EventDto
import com.example.techevents.data.local.toDomain
import com.example.techevents.data.local.toEntity
import com.example.techevents.domain.model.Event
import com.example.techevents.domain.repository.EventRepository
import retrofit2.HttpException
import java.io.IOException

class EventRepositoryImpl(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource
) : EventRepository {

    override suspend fun getEvents(
        page: Int,
        limit: Int,
        query: String,
        category: String,
        isOnline: Boolean?
    ): Result<List<Event>> {
        return runCatching {
            val events = remote.getEvents(
                page = page,
                limit = limit,
                search = query.ifBlank { null },
                category = category.ifBlank { null },
                isOnline = isOnline
            ).map { it.toDomain() }

            if (page == 1 && query.isBlank() && category.isBlank() && isOnline == null) {
                local.clearAll()
                local.insertAll(events.map { it.toEntity() })
            }
            events
        }.recoverCatching { e ->
            if (page == 1 && query.isBlank() && category.isBlank() && isOnline == null) {
                val cached = local.getAll()
                if (cached.isNotEmpty()) cached.map { it.toDomain() }
                else throw mapException(e)
            } else throw mapException(e)
        }
    }

    override suspend fun getEventById(id: String): Result<Event> =
        runCatching { remote.getEventById(id).toDomain() }
            .recoverCatching { throw mapException(it) }

    override suspend fun createEvent(
        title: String, description: String, date: String, time: String,
        location: String, category: String, isOnline: Boolean, capacity: Int, link: String?
    ): Result<Event> = runCatching {
        val request = CreateEventRequest(title, description, date, time, location, category, isOnline, capacity, link = link)
        remote.createEvent(request).toDomain()
    }.recoverCatching { throw mapException(it) }

    override suspend fun updateEvent(
        id: String, title: String, description: String, date: String, time: String,
        location: String, category: String, isOnline: Boolean, capacity: Int, link: String?
    ): Result<Event> = runCatching {
        val request = CreateEventRequest(title, description, date, time, location, category, isOnline, capacity, link = link)
        remote.updateEvent(id, request).toDomain()
    }.recoverCatching { throw mapException(it) }

    override suspend fun deleteEvent(id: String): Result<Unit> =
        runCatching { remote.deleteEvent(id) }
            .recoverCatching { throw mapException(it) }

    private fun mapException(e: Throwable): Exception = when (e) {
        is IOException -> Exception("Sem conexão com a internet", e)
        is HttpException -> Exception("Erro no servidor (${e.code()})", e)
        else -> Exception(e.message ?: "Erro desconhecido", e)
    }

    private fun EventDto.toDomain() = Event(
        id = id, title = title, description = description, date = date, time = time,
        location = location, category = category, isOnline = isOnline, capacity = capacity,
        enrolled = enrolled, imageUrl = imageUrl, link = link
    )
}
