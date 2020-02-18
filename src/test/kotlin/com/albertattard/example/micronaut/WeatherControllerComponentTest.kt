package com.albertattard.example.micronaut

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest

@MicronautTest
class WeatherControllerComponentTest(
    @Client("/weather") private val client: RxHttpClient
) : StringSpec({
    "should return the same forecast irrespective of the number of times it is requested" {

        val request = HttpRequest.GET<Forecast>("/forecast")
            .basicAuth("micronaut", "framework")

        val firstForecast = client.toBlocking().retrieve(request, Forecast::class.java)

        repeat(10) {
            val forecast = client.toBlocking().retrieve(request, Forecast::class.java)
            forecast shouldBe firstForecast
        }
    }
})
