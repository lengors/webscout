package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DucklingDurationResponseValue(
    @JsonProperty("unit")
    val unit: DucklingGrain,
    @JsonProperty("normalized")
    val normalized: DucklingDurationNormalizedResponseValue,
) : DucklingDateTimeLikeResponseValue
