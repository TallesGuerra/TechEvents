package com.example.techevents.data.api

import com.example.techevents.data.dto.EventDto
import com.example.techevents.data.dto.TechEventsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TechEventsApi {

    @GET("events/search/")
    suspend fun getEvents(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 20,
        @Query("q") query: String? = null,
        @Query("categories") categories: String? = null,
        @Query("online_events_only") onlineOnly: Boolean? = null,
        @Query("expand") expand: String = "venue,logo"
    ): TechEventsResponse

    @GET("events/{id}/")
    suspend fun getEventById(
        @Path("id") id: String,
        @Query("expand") expand: String = "venue,logo"
    ): EventDto
}
