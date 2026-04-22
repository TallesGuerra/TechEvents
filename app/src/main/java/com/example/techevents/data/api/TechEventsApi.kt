package com.example.techevents.data.api

import com.example.techevents.data.dto.MeetupEventDto
import com.example.techevents.data.dto.MeetupResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TechEventsApi {

    @GET("find/upcoming_events")
    suspend fun getUpcomingEvents(
        @Query("text") text: String = "",
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null,
        @Query("online_events") onlineEvents: Boolean? = null
    ): MeetupResponse

    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") id: String
    ): MeetupEventDto
}
