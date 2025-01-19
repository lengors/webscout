package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DucklingAmountOfMoneyResponseValue(
    @JsonProperty("value")
    val value: Double,
    @JsonProperty("unit")
    val unit: String,
) : DucklingResponseValue
