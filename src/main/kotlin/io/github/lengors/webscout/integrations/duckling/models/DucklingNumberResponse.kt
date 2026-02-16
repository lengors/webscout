package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DucklingNumberResponse(
    @JsonProperty(DucklingResponse.VALUE)
    override val value: DucklingNumberResponseValue,
) : DucklingAmountOfMoneyLikeResponse {
    @JsonProperty(DucklingResponse.DIMENSION)
    override val dimension: DucklingNumberDimension = DucklingNumberDimension
}
