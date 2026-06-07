package io.github.lengors.webscout.domain.network.http.services

import io.github.lengors.webscout.domain.network.ssl.models.SslMaterial

interface HttpExchangerProvider {
    fun provide(sslMaterial: SslMaterial? = null): HttpExchanger
}
