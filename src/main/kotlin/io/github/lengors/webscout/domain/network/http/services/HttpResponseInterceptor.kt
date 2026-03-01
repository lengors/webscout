package io.github.lengors.webscout.domain.network.http.services

import io.github.lengors.webscout.domain.network.http.models.HttpResponse

interface HttpResponseInterceptor {
    fun intercept(
        response: HttpResponse,
        session: HttpSession,
    ): HttpResponse
}
