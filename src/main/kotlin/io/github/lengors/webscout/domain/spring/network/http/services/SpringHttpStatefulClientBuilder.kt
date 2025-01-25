package io.github.lengors.webscout.domain.spring.network.http.services

import io.github.lengors.webscout.domain.network.http.services.HttpStateManagerBuilder
import io.github.lengors.webscout.domain.network.http.services.HttpStatefulClient
import io.github.lengors.webscout.domain.network.http.services.HttpStatefulClientBuilder
import io.github.lengors.webscout.domain.network.ssl.models.SslMaterial
import org.springframework.web.reactive.function.client.WebClient

class SpringHttpStatefulClientBuilder(
    private val webClientBuilder: WebClient.Builder,
    private val stateManagerBuilder: HttpStateManagerBuilder,
) : HttpStatefulClientBuilder {
    override fun build(sslMaterial: SslMaterial?): HttpStatefulClient =
        SpringHttpStatefulClient(stateManagerBuilder, webClientBuilder, sslMaterial)
}
