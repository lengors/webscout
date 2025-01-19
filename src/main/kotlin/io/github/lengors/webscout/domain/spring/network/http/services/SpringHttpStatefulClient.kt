package io.github.lengors.webscout.domain.spring.network.http.services

import io.github.lengors.webscout.domain.network.http.models.HttpRequest
import io.github.lengors.webscout.domain.network.http.models.HttpResponse
import io.github.lengors.webscout.domain.network.http.services.HttpStateManager
import io.github.lengors.webscout.domain.network.http.services.HttpStateManagerBuilder
import io.github.lengors.webscout.domain.network.http.services.HttpStatefulClient
import io.github.lengors.webscout.domain.network.ssl.models.SslMaterial
import io.github.lengors.webscout.domain.utilities.asMultiValueMap
import io.netty.handler.ssl.SslContextBuilder
import org.springframework.http.HttpCookie
import org.springframework.http.HttpMethod
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchangeOrNull
import org.springframework.web.reactive.function.client.createExceptionAndAwait
import reactor.netty.http.client.HttpClient
import reactor.netty.http.client.HttpClientRequest
import java.net.URI

class SpringHttpStatefulClient(
    stateManagerBuilder: HttpStateManagerBuilder,
    webClientBuilder: WebClient.Builder,
    sslMaterial: SslMaterial? = null,
) : HttpStatefulClient {
    private val stateManager: HttpStateManager by lazy(stateManagerBuilder::build)
    private val webClient: WebClient by lazy {
        webClientBuilder
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient
                        .create()
                        .followRedirect(true)
                        .let { httpClient ->
                            sslMaterial
                                ?.let {
                                    SslContextBuilder
                                        .forClient()
                                        .ciphers(sslMaterial.ciphers)
                                        .keyManager(sslMaterial.keyManagerFactory)
                                        .protocols(sslMaterial.protocols)
                                        .trustManager(sslMaterial.trustManagerFactory)
                                        .build()
                                }?.let { sslContext ->
                                    httpClient.secure { it.sslContext(sslContext) }
                                }
                                ?: httpClient
                        },
                ),
            ).build()
    }

    override suspend fun exchange(request: HttpRequest): HttpResponse =
        mutableListOf<HttpClientRequest>().let { httpClientRequests ->
            webClient
                .method(HttpMethod.valueOf(request.method.name))
                .uri(request.uri)
                .headers { it.addAll(request.headers.asMultiValueMap()) }
                .let { request.body?.let(it::bodyValue) ?: it }
                .httpRequest { httpRequest ->
                    httpRequest.cookies.addAll(
                        stateManager[httpRequest.uri]
                            .associate { it.name to HttpCookie(it.name, it.value) }
                            .asMultiValueMap(),
                    )
                    httpClientRequests.add(httpRequest.getNativeRequest())
                }.awaitExchangeOrNull { exchange ->
                    httpClientRequests
                        .lastOrNull()
                        ?.resourceUrl()
                        ?.let(URI::create)
                        .let { it ?: exchange.request().uri }
                        .let { exchangeUri ->
                            stateManager[exchangeUri] =
                                exchange
                                    .headers()
                                    .asHttpHeaders()
                            val statusCode = exchange.statusCode()
                            if (statusCode.is2xxSuccessful) {
                                exchange
                                    .awaitBody<String>()
                                    .let { response ->
                                        HttpResponse(exchangeUri, statusCode.value(), response)
                                    }
                            } else {
                                throw exchange.createExceptionAndAwait()
                            }
                        }
                }
                ?: throw IllegalStateException("Await exchange response is missing")
        }
}
