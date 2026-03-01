package io.github.lengors.webscout.domain.network.http.services

import io.github.lengors.webscout.domain.network.http.models.HttpRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

@Component
class AuthorizationHttpRequestInterceptor : HttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        session: HttpSession,
    ): HttpRequest =
        request.copy(
            headers =
                request.headers +
                    mapOf(
                        HttpHeaders.AUTHORIZATION to (
                            request.headers[HttpHeaders.AUTHORIZATION]
                                ?.also { session.setHeader(HttpHeaders.AUTHORIZATION, it) }
                                ?: session.getHeader(HttpHeaders.AUTHORIZATION)
                        ),
                    ),
        )
}
