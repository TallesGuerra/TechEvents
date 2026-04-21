package com.example.techevents.data.dto

// A API do Meetup retorna um array JSON diretamente.
// Use List<MeetupEventDto> como tipo de retorno no Retrofit, não este wrapper.
// Este typealias existe apenas para legibilidade caso queira nomear o tipo.
typealias MeetupResponse = List<MeetupEventDto>
