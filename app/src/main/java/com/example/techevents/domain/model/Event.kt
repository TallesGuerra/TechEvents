package com.example.techevents.domain.model

data class Event(
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
    val imageUrl: String?
)
