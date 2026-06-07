package io.github.lengors.webscout.domain.network.ssl.models

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory

interface SslMaterial {
    val ciphers: Set<String>

    val keyManagerFactory: KeyManagerFactory

    val protocols: Set<String>

    val trustManagerFactory: TrustManagerFactory
}
