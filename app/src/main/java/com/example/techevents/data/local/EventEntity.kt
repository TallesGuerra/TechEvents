package com.example.techevents.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.techevents.domain.model.Event

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val category: String,
    val isOnline: Boolean,
    val capacity: Int,
    val enrolled: Int,
    val imageUrl: String?,
    val link: String?
)

fun EventEntity.toDomain() = Event(
    id, title, description, date, time, location, category, isOnline, capacity, enrolled, imageUrl, link
)

fun Event.toEntity() = EventEntity(
    id, title, description, date, time, location, category, isOnline, capacity, enrolled, imageUrl, link
)
