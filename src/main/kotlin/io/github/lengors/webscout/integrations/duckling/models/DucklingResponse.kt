package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonProperty

sealed interface DucklingResponse {
    companion object Properties {
        const val DIMENSION = "dim"
        const val VALUE = "value"
    }

    @get:JsonProperty(DIMENSION)
    val dimension: DucklingDimension

    @get:JsonProperty(VALUE)
    val value: DucklingResponseValue
}
