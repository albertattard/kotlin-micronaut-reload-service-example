package com.albertattard.example.micronaut

import io.micronaut.runtime.context.scope.Refreshable
import javax.annotation.PostConstruct
import org.slf4j.LoggerFactory

@Refreshable
class WeatherService {

    private val logger = LoggerFactory.getLogger(WeatherService::class.java)

    private var forecast: Forecast = Forecast("Unknown")

    @PostConstruct
    fun init() {
        forecast = Forecast("Scattered Clouds")
        logger.debug("Updating weather forecast: $forecast")
    }

    fun latestForecast(): Forecast {
        return forecast
    }
}
