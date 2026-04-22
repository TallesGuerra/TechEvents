package com.example.techevents.data.dto

import com.google.gson.annotations.SerializedName

data class TechEventsResponse(
    val events: List<EventDto>,
    val pagination: PaginationDto
)

data class PaginationDto(
    @SerializedName("has_more_items") val hasMoreItems: Boolean,
    @SerializedName("page_number") val pageNumber: Int,
    @SerializedName("page_count") val pageCount: Int
)
