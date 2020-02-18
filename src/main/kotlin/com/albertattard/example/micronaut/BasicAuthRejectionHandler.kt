package com.albertattard.example.micronaut

import io.micronaut.context.annotation.Replaces
import io.micronaut.core.async.publisher.Publishers
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.security.handlers.HttpStatusCodeRejectionHandler
import io.micronaut.security.handlers.RejectionHandler
import javax.inject.Singleton
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory

@Singleton
@Replaces(HttpStatusCodeRejectionHandler::class)
class BasicAuthRejectionHandler : RejectionHandler {

    private val logger = LoggerFactory.getLogger(BasicAuthRejectionHandler::class.java)

    override fun reject(request: HttpRequest<*>?, forbidden: Boolean): Publisher<MutableHttpResponse<*>> {
        logger.warn("Rejecting request: $request (forbidden: $forbidden)")
        val httpResponse = HttpResponse
            .status<Any>(if (forbidden) HttpStatus.FORBIDDEN else HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.TEXT_PLAIN_TYPE)
            .header("WWW-Authenticate", "Basic")
        return Publishers.just(httpResponse)
    }
}
