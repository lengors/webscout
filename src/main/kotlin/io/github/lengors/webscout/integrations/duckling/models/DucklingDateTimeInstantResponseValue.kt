package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class DucklingDateTimeInstantResponseValue(
    @JsonProperty("value")
    val value: ZonedDateTime,
    @JsonProperty("grain")
    val grain: DucklingGrain,
) : DucklingDateTimeResponseValue
