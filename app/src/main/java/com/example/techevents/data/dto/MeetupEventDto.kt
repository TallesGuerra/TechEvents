package com.example.techevents.data.dto

data class EventDto(
    val id: String,
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
