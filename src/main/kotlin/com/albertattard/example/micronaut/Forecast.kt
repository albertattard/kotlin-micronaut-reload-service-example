package com.albertattard.example.micronaut

import java.time.LocalDateTime

data class Forecast(
    val caption: String,
    val dateTime: LocalDateTime = LocalDateTime.now()
)
