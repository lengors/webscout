package io.github.lengors.webscout.domain.network.http.services

import io.github.lengors.webscout.domain.network.http.models.HttpRequest
import io.github.lengors.webscout.domain.network.http.models.HttpResponse
import org.springframework.stereotype.Component

@Component
class SessionHttpInterceptor : HttpRequestInterceptor, HttpResponseInterceptor {
    override fun intercept(
        request: HttpRequest,
        session: HttpSession
    ): HttpRequest = request.copy(cookies = request.cookies + session.getCookies(request.uri))

    override fun intercept(
        response: HttpResponse,
        session: HttpSession
    ): HttpResponse = response.also {
        session.update(it.uri, it.headers)
    }
}