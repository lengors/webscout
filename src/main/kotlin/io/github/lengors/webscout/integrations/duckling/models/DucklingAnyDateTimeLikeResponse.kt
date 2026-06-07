package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DucklingAnyDateTimeLikeResponse(
    @JsonProperty(DucklingResponse.VALUE)
    override val value: DucklingAnyDateTimeLikeResponseValue,
) : DucklingDateTimeLikeResponse {
    @JsonIgnore
    override val dimension: DucklingAnyDateTimeLikeDimension = DucklingAnyDateTimeLikeDimension
}
