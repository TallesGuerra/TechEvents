package com.example.techevents.data.dto

data class CreateEventRequest(
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val category: String,
    val isOnline: Boolean,
    val capacity: Int,
    val enrolled: Int = 0,
    val imageUrl: String? = null,
    val link: String? = null
)
