package io.github.lengors.webscout.domain.network.http.services

import java.net.HttpCookie
import java.net.URI

interface HttpStateManager {
    operator fun get(uri: URI): List<HttpCookie>

    operator fun set(
        uri: URI,
        headers: Map<String, List<String>>,
    )
}
