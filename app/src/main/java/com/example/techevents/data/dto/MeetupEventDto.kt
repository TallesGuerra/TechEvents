package com.example.techevents.data.dto

import com.google.gson.annotations.SerializedName

data class EventDto(
    val id: String,
    val name: EventTextDto,
    val description: EventTextDto?,
    val start: EventDateDto,
    @SerializedName("online_event") val onlineEvent: Boolean,
    val url: String?,
    val capacity: Int?,
    val logo: EventLogoDto?,
    val venue: EventVenueDto?
)

data class EventTextDto(val text: String?)

data class EventDateDto(val local: String?)

data class EventLogoDto(val url: String?)

data class EventVenueDto(
    val name: String?,
    val address: EventAddressDto?
)

data class EventAddressDto(
    @SerializedName("localized_address_display") val localizedAddress: String?
)
