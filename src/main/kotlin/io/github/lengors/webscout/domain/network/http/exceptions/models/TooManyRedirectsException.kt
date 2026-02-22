package io.github.lengors.webscout.domain.network.http.exceptions.models

import io.github.lengors.webscout.domain.network.http.models.HttpResponse

class TooManyRedirectsException(
    val response: HttpResponse
) : RuntimeException("Too many redirects")
