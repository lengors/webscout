package io.github.lengors.webscout.domain.network.http.models

import java.net.URI

private val DEFAULT_PORTS = mapOf(
    "http" to 80,
    "https" to 443
)

fun URI.isSameOrigin(target: URI): Boolean {
    if (host != target.host) {
        return false
    }

    val targetDefaultPorts = setOfNotNull(DEFAULT_PORTS[target.scheme]).plus(-1)
    if (target.scheme == "http" &&
        target.port in targetDefaultPorts &&
        scheme == "https" &&
        port in setOfNotNull(DEFAULT_PORTS[scheme]).plus(-1)
    ) {
        return true
    }

    val changedScheme = target.scheme != scheme
    if (!changedScheme && target.port in targetDefaultPorts && port in targetDefaultPorts) {
        return true
    }

    return port == target.port
}
