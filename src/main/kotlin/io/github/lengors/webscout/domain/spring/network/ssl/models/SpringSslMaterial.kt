package io.github.lengors.webscout.domain.spring.network.ssl.models

import io.github.lengors.webscout.domain.network.ssl.models.SslMaterial
import org.springframework.boot.ssl.SslBundle
import org.springframework.boot.ssl.SslOptions
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory

@JvmInline
value class SpringSslMaterial(
    private val sslBundle: SslBundle,
) : SslMaterial {
    override val ciphers: Set<String>
        get() = SslOptions.asSet(sslBundle.options.ciphers)

    override val keyManagerFactory: KeyManagerFactory
        get() = sslBundle.managers.keyManagerFactory

    override val protocols: Set<String>
        get() = SslOptions.asSet(sslBundle.options.enabledProtocols)

    override val trustManagerFactory: TrustManagerFactory
        get() = sslBundle.managers.trustManagerFactory
}
