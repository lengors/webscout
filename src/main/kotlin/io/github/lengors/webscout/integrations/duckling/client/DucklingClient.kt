package io.github.lengors.webscout.integrations.duckling.client

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.lengors.webscout.domain.utilities.asMultiValueMap
import io.github.lengors.webscout.integrations.duckling.models.DucklingDimension
import io.github.lengors.webscout.integrations.duckling.models.DucklingRequest
import io.github.lengors.webscout.integrations.duckling.models.DucklingResponse
import io.github.lengors.webscout.integrations.duckling.properties.DucklingClientConnectionDetails
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import kotlin.reflect.cast

@Component
class DucklingClient(
    restTemplateBuilder: RestTemplateBuilder,
    ducklingClientConnectionDetails: DucklingClientConnectionDetails,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        const val PARSE_ENDPOINT = "/parse"
    }

    private val restTemplate: RestTemplate by lazy {
        restTemplateBuilder
            .requestFactory(JdkClientHttpRequestFactory::class.java)
            .rootUri(ducklingClientConnectionDetails.url)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    fun <T : DucklingDimension, U : DucklingResponse<T, *>, V : DucklingRequest<T, U>> parse(request: V): U? =
        restTemplate
            .postForObject(
                PARSE_ENDPOINT,
                objectMapper.asMultiValueMap(request),
                request.responseType.java.arrayType(),
            )?.let(Array::class::cast)
            ?.firstOrNull()
            ?.let(request.responseType::cast)
}
