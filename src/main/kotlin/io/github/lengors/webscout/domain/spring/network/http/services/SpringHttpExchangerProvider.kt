package io.github.lengors.webscout.domain.spring.network.http.services

import io.github.lengors.webscout.domain.network.http.services.HttpExchanger
import io.github.lengors.webscout.domain.network.http.services.HttpExchangerProvider
import io.github.lengors.webscout.domain.network.http.services.HttpRequestInterceptor
import io.github.lengors.webscout.domain.network.http.services.HttpResponseInterceptor
import io.github.lengors.webscout.domain.network.ssl.models.SslMaterial
import io.github.lengors.webscout.domain.spring.network.http.properties.SpringHttpExchangerProperties
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class SpringHttpExchangerProvider(
    private val webClientBuilder: WebClient.Builder,
    private val httpExchangerProperties: SpringHttpExchangerProperties,
    private val httpRequestInterceptors: List<HttpRequestInterceptor>,
    private val httpResponseInterceptors: List<HttpResponseInterceptor>,
) : HttpExchangerProvider {
    override fun provide(sslMaterial: SslMaterial?): HttpExchanger =
        SpringHttpExchanger(
            webClientBuilder,
            httpExchangerProperties,
            httpRequestInterceptors,
            httpResponseInterceptors,
            sslMaterial
        )
}