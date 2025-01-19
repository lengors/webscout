package io.github.lengors.webscout.domain.network.ssl.services

import io.github.lengors.webscout.domain.network.ssl.models.SslMaterial
import org.springframework.stereotype.Service

@Service
interface SslMaterialLoader {
    fun loadCertificates(certificates: String?): SslMaterial?
}

fun SslMaterialLoader.loadCertificates(certificates: Iterable<String>?): SslMaterial? =
    certificates
        ?.joinToString(System.lineSeparator())
        ?.let(::loadCertificates)
