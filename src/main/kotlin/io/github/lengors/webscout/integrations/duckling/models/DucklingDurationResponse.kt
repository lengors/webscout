package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DucklingDurationResponse(
    @JsonProperty(DucklingResponse.VALUE)
    override val value: DucklingDurationResponseValue,
) : DucklingDateTimeLikeResponse {
    @JsonProperty(DucklingResponse.DIMENSION)
    override val dimension: DucklingDurationDimension = DucklingDurationDimension
}
