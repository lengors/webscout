package io.github.lengors.webscout.domain.network.http.models

import java.net.URI

data class HttpResponse(
    val uri: URI,
    val statusCode: Int,
    val body: String,
)
