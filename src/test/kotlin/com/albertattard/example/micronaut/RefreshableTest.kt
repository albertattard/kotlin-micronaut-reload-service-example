package com.albertattard.example.micronaut

import io.kotlintest.matchers.date.shouldBeBefore
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest

@MicronautTest
class RefreshableTest(
    @Client("/") private val client: RxHttpClient
) : StringSpec({
    "!should refresh the weather service and return an updated forecast" {
        val forecastBeforeRefresh = client.toBlocking().retrieve("/weather/forecast", Forecast::class.java)

        client.toBlocking().exchange(HttpRequest.POST("/refresh", mapOf("force" to "true")), String::class.java)

        val forecastAfterRefresh = client.toBlocking().retrieve("/weather/forecast", Forecast::class.java)
        forecastBeforeRefresh.caption shouldBe forecastAfterRefresh.caption
        forecastBeforeRefresh.dateTime shouldBeBefore forecastAfterRefresh.dateTime
    }
})
