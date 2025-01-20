package io.github.lengors.webscout.domain.network.http.services

import io.github.lengors.webscout.domain.network.http.models.HttpRequest
import io.github.lengors.webscout.domain.network.http.models.HttpResponse

interface HttpStatefulClient {
    suspend fun exchange(request: HttpRequest): HttpResponse
}
