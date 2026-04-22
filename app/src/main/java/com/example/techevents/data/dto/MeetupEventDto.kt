package com.example.techevents.data.dto

import com.google.gson.annotations.SerializedName

data class MeetupEventDto(
    val id: String,
    val name: String,
    val description: String?,
    @SerializedName("local_date") val localDate: String?,
    @SerializedName("local_time") val localTime: String?,
    val venue: VenueDto?,
    val group: GroupDto?,
    @SerializedName("yes_rsvp_count") val yesRsvpCount: Int = 0,
    @SerializedName("rsvp_limit") val rsvpLimit: Int?,
    @SerializedName("featured_photo") val featuredPhoto: PhotoDto?,
    @SerializedName("is_online_event") val isOnline: Boolean = false
)

data class VenueDto(
    val name: String?,
    @SerializedName("address_1") val address: String?,
    val city: String?
)

data class GroupDto(
    val name: String?,
    val urlname: String?
)

data class PhotoDto(
    @SerializedName("photo_link") val photoUrl: String?
)
