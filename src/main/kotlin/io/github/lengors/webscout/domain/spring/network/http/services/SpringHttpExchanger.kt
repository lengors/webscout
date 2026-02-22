package io.github.lengors.webscout.domain.spring.network.http.services

import io.github.lengors.webscout.domain.network.http.exceptions.models.TooManyRedirectsException
import io.github.lengors.webscout.domain.network.http.models.HttpMethod
import io.github.lengors.webscout.domain.network.http.models.HttpRequest
import io.github.lengors.webscout.domain.network.http.models.HttpResponse
import io.github.lengors.webscout.domain.network.http.models.isSameOrigin
import io.github.lengors.webscout.domain.network.http.services.HttpExchanger
import io.github.lengors.webscout.domain.network.http.services.HttpRequestInterceptor
import io.github.lengors.webscout.domain.network.http.services.HttpResponseInterceptor
import io.github.lengors.webscout.domain.network.http.services.HttpSession
import io.github.lengors.webscout.domain.network.ssl.models.SslMaterial
import io.github.lengors.webscout.domain.spring.network.http.properties.SpringHttpExchangerProperties
import io.github.lengors.webscout.domain.utilities.asMultiValueMap
import io.netty.handler.ssl.SslContextBuilder
import org.springframework.http.HttpCookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.HttpMethod as SpringHttpMethod
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.springframework.web.reactive.function.client.awaitExchangeOrNull
import org.springframework.web.reactive.function.client.createExceptionAndAwait
import reactor.netty.http.client.HttpClient
import reactor.netty.http.client.HttpClientRequest
import java.net.URI

class SpringHttpExchanger(
    webClientBuilder: WebClient.Builder,
    private val httpExchangerProperties: SpringHttpExchangerProperties,
    private val httpRequestInterceptors: List<HttpRequestInterceptor>,
    private val httpResponseInterceptors: List<HttpResponseInterceptor>,
    sslMaterial: SslMaterial? = null,
) : HttpExchanger {
    companion object {
        private val MANUAL_REDIRECTORS = HttpStatus.FOUND.value()..HttpStatus.SEE_OTHER.value()
    }

    private val webClient = webClientBuilder
        .clone()
        .clientConnector(
            ReactorClientHttpConnector(
                HttpClient
                    .create()
                    .followRedirect { _, response ->
                        response
                            .status()
                            .code()
                            .let { it !in MANUAL_REDIRECTORS && HttpStatusCode.valueOf(it).is3xxRedirection }
                    }
                    .wiretap(true)
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
            )
        )
        .build()

    override suspend fun exchange(session: HttpSession, request: HttpRequest): HttpResponse {
        var redirects = httpExchangerProperties.maxRedirectsAllowed
        var response = submitExchange(session, request)

        while (redirects-- > 0 && response.statusCode in MANUAL_REDIRECTORS) {

            // Resolve a new location
            val location = response.headers[HttpHeaders.LOCATION]?.firstOrNull()
            val uri = location?.let(response.uri::resolve) ?: response.uri

            // Update request method
            val requestMethod = request.method
                .takeIf { it == HttpMethod.HEAD }
                ?: HttpMethod.GET

            // Updated headers
            val headers = if (uri.isSameOrigin(request.uri)) request.headers else request.headers.filterNot {
                it.key.equals(
                    HttpHeaders.AUTHORIZATION,
                    ignoreCase = true
                )
            }

            // Submit a new request with updated method and headers
            val currentRequest = HttpRequest(
                uri,
                requestMethod,
                if (requestMethod != request.method) headers.filterNot {
                    it.key.equals(
                        HttpHeaders.CONTENT_TYPE,
                        ignoreCase = true
                    )
                } else headers,
                request.body.takeIf { requestMethod == request.method }
            )
            response = submitExchange(session, currentRequest)
        }

        return response
            .takeUnless { response.statusCode in MANUAL_REDIRECTORS && redirects <= 0 }
            ?: throw TooManyRedirectsException(response)
    }

    private suspend fun submitExchange(
        stateManager: HttpSession,
        request: HttpRequest
    ): HttpResponse = httpRequestInterceptors
        .fold(request) { accumulator, interceptor ->
            interceptor.intercept(accumulator, stateManager)
        }.let { computedRequest ->
            mutableListOf<HttpClientRequest>().let { httpClientRequests ->
                webClient
                    .method(SpringHttpMethod.valueOf(request.method.name))
                    .uri(request.uri)
                    .headers { it.addAll(request.headers.asMultiValueMap()) }
                    .let { request.body?.let(it::bodyValue) ?: it }
                    .httpRequest { httpRequest ->
                        httpRequest.cookies.addAll(
                            computedRequest.cookies
                                .mapValues { HttpCookie(it.key, it.value) }
                                .asMultiValueMap(),
                        )
                        httpClientRequests.add(httpRequest.getNativeRequest())
                    }.awaitExchangeOrNull { exchange ->
                        val body = runCatching {
                            exchange.awaitBodyOrNull<String>()
                        }.getOrNull()
                        httpClientRequests
                            .lastOrNull()
                            ?.resourceUrl()
                            ?.let(URI::create)
                            .let { it ?: exchange.request().uri }
                            .let { exchangeUri ->
                                val statusCode = exchange.statusCode()
                                if (statusCode.is2xxSuccessful || statusCode.is3xxRedirection) {
                                    httpResponseInterceptors.fold(
                                        HttpResponse(
                                            exchangeUri,
                                            statusCode.value(),
                                            body,
                                            exchange
                                                .headers()
                                                .asHttpHeaders()
                                        )
                                    ) { accumulator, interceptor ->
                                        interceptor.intercept(accumulator, stateManager)
                                    }
                                } else {
                                    throw exchange.createExceptionAndAwait()
                                }
                            }
                    }
                    ?: throw IllegalStateException("Await exchange response is missing")
            }
        }
}