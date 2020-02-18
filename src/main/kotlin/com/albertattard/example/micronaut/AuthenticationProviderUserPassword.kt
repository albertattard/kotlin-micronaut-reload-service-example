package com.albertattard.example.micronaut

import io.micronaut.security.authentication.AuthenticationFailed
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.UserDetails
import io.reactivex.Flowable
import java.util.ArrayList
import javax.inject.Singleton
import org.reactivestreams.Publisher

@Singleton
class AuthenticationProviderUserPassword : AuthenticationProvider {
    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>): Publisher<AuthenticationResponse> =
        if (authenticationRequest.identity == "micronaut" && authenticationRequest.secret == "framework")
            Flowable.just(UserDetails(authenticationRequest.identity as String, ArrayList()))
        else
            Flowable.just(AuthenticationFailed())
}
