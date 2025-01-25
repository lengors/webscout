package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DucklingDateTimeResponse(
    @JsonProperty(DucklingResponse.VALUE)
    override val value: DucklingDateTimeResponseValue,
) : DucklingResponse<DucklingDateTimeDimension, DucklingDateTimeResponseValue> {
    @JsonProperty(DucklingResponse.DIMENSION)
    override val dimension: DucklingDateTimeDimension = DucklingDateTimeDimension
}
