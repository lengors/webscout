package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DucklingDateTimeRangeResponseValue(
    @JsonProperty("from")
    val from: DucklingDateTimeInstantResponseValue,
    @JsonProperty("to")
    val to: DucklingDateTimeInstantResponseValue,
) : DucklingDateTimeResponseValue
