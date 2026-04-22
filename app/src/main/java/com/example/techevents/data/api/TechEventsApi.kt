package com.example.techevents.data.api

import com.example.techevents.data.dto.CreateEventRequest
import com.example.techevents.data.dto.EventDto
import com.example.techevents.data.dto.TechEventsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TechEventsApi {

    @GET("events")
    suspend fun getEvents(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("isOnline") isOnline: Boolean? = null
    ): TechEventsResponse

    @GET("events/{id}")
    suspend fun getEventById(
        @Path("id") id: String
    ): EventDto

    @POST("events")
    suspend fun createEvent(
        @Body request: CreateEventRequest
    ): EventDto
}
