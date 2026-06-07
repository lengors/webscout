package io.github.lengors.webscout.domain.spring.network.ssl.services

import io.github.lengors.webscout.domain.network.ssl.models.SslMaterial
import io.github.lengors.webscout.domain.network.ssl.services.SslMaterialLoader
import io.github.lengors.webscout.domain.spring.network.ssl.models.SpringSslMaterial
import org.springframework.boot.web.server.Ssl
import org.springframework.boot.web.server.WebServerSslBundle

class SpringSslMaterialLoader : SslMaterialLoader {
    override fun loadCertificates(certificates: String?): SslMaterial? =
        certificates
            ?.takeUnless(String::isBlank)
            ?.let {
                Ssl().apply {
                    isEnabled = true
                    trustCertificate = it
                }
            }?.let(WebServerSslBundle::get)
            ?.let(::SpringSslMaterial)
}
