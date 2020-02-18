package com.albertattard.example.micronaut

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller("/weather")
@Secured(SecurityRule.IS_AUTHENTICATED)
class WeatherController internal constructor(
    private var service: WeatherService
) {

    @Get("/forecast", produces = [MediaType.APPLICATION_JSON])
    fun forecast(): Forecast {
        return service.latestForecast()
    }
}
