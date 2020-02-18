package com.albertattard.example.micronaut

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.kotlintest.MicronautKotlinTestExtension.getMock
import io.mockk.called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@MicronautTest
class WeatherControllerTest(
    private val service: WeatherService,
    @Client("/weather") private val client: RxHttpClient
) : StringSpec({
    "should return the forecast returned by the weather service" {
        val mock = getMock(service)

        val forecast = Forecast("Sunny Micronaut Framework")
        every { mock.latestForecast() } returns forecast

        val request = HttpRequest.GET<Forecast>("/forecast")
            .basicAuth("micronaut", "framework")

        val result = client.toBlocking().retrieve(request, Forecast::class.java)
        result shouldBe forecast

        verify(exactly = 1) { mock.latestForecast() }
        confirmVerified(mock)
    }

    "should return a 401 with the WWW-Authenticate set when accessed without credentials" {
        val mock = getMock(service)

        val forecast = Forecast("Sunny Micronaut Framework")
        every { mock.latestForecast() } returns forecast

        val exception = shouldThrow<HttpClientResponseException> {
            val request = HttpRequest.GET<Forecast>("/forecast")
            client.toBlocking().retrieve(request, Forecast::class.java)
        }
        exception.status shouldBe HttpStatus.UNAUTHORIZED
        exception.response.header("WWW-Authenticate") shouldBe "Basic"

        verify { mock wasNot called }
        confirmVerified(mock)
    }

    "should return a 401 with the WWW-Authenticate set when accessed with invalid credentials" {
        val mock = getMock(service)

        val forecast = Forecast("Sunny Micronaut Framework")
        every { mock.latestForecast() } returns forecast

        val exception = shouldThrow<HttpClientResponseException> {
            val request = HttpRequest.GET<Forecast>("/forecast")
                .basicAuth("something", "else")
            client.toBlocking().retrieve(request, Forecast::class.java)
        }
        exception.status shouldBe HttpStatus.UNAUTHORIZED
        exception.response.header("WWW-Authenticate") shouldBe "Basic"

        verify { mock wasNot called }
        confirmVerified(mock)
    }
}) {
    @MockBean(WeatherService::class)
    fun weatherService(): WeatherService {
        return mockk()
    }
}
