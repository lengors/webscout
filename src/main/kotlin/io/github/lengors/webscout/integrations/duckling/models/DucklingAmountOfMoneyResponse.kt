package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DucklingAmountOfMoneyResponse(
    @JsonProperty(DucklingResponse.VALUE)
    override val value: DucklingAmountOfMoneyResponseValue,
) : DucklingResponse<DucklingAmountOfMoneyDimension, DucklingAmountOfMoneyResponseValue> {
    @JsonProperty(DucklingResponse.DIMENSION)
    override val dimension: DucklingAmountOfMoneyDimension = DucklingAmountOfMoneyDimension
}
