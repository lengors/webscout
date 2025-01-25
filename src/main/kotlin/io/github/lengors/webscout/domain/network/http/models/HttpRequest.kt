package io.github.lengors.webscout.domain.network.http.models

import java.net.URI

data class HttpRequest(
    val uri: URI,
    val method: HttpMethod,
    val headers: Map<String, String?> = emptyMap(),
    val body: Map<String, List<String?>>? = null,
)
