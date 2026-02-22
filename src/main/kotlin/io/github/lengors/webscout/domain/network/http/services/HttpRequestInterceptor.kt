package io.github.lengors.webscout.domain.network.http.services

import io.github.lengors.webscout.domain.network.http.models.HttpRequest

interface HttpRequestInterceptor {
    fun intercept(request: HttpRequest, session: HttpSession): HttpRequest
}
