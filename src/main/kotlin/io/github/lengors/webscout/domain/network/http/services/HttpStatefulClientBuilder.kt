package io.github.lengors.webscout.domain.network.http.services

import io.github.lengors.webscout.domain.network.ssl.models.SslMaterial
import org.springframework.stereotype.Service

@Service
interface HttpStatefulClientBuilder {
    fun build(sslMaterial: SslMaterial? = null): HttpStatefulClient
}
