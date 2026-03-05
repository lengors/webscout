package io.github.lengors.webscout.integrations.duckling.client

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.webscout.domain.exceptions.findInStackTrace
import io.github.lengors.webscout.domain.utilities.asMultiValueMap
import io.github.lengors.webscout.integrations.duckling.models.DucklingRequest
import io.github.lengors.webscout.integrations.duckling.models.DucklingResponse
import io.github.lengors.webscout.integrations.duckling.properties.DucklingClientConnectionDetails
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.PrematureCloseException
import reactor.util.retry.Retry
import java.time.Duration

@Component
class DucklingClient(
    webClientBuilder: WebClient.Builder,
    ducklingClientConnectionDetails: DucklingClientConnectionDetails,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val PARSE_ENDPOINT = "/parse"
    }

    private val webClient: WebClient =
        webClientBuilder
            .clone()
            .baseUrl(ducklingClientConnectionDetails.url)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()

    suspend fun <T : DucklingResponse, U : DucklingRequest<T>> parse(request: U): List<T> =
        webClient
            .post()
            .uri(PARSE_ENDPOINT)
            .bodyValue(objectMapper.asMultiValueMap(request))
            .retrieve()
            .bodyToFlux(request.responseType.java)
            .retryWhen(
                Retry
                    .backoff(3, Duration.ofMillis(500))
                    .filter { it.findInStackTrace<PrematureCloseException>() != null },
            ).asFlow()
            .toList()
}
