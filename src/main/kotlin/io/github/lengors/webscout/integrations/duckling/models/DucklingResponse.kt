package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = DucklingResponse.DIMENSION,
)
@JsonSubTypes(
    JsonSubTypes.Type(value = DucklingAmountOfMoneyResponse::class, name = DucklingAmountOfMoneyDimension.VALUE),
    JsonSubTypes.Type(value = DucklingDateTimeResponse::class, name = DucklingDateTimeDimension.VALUE),
)
sealed interface DucklingResponse<T : DucklingDimension, U : DucklingResponseValue> {
    companion object Properties {
        const val DIMENSION = "dim"
        const val VALUE = "value"
    }

    @get:JsonProperty(DIMENSION)
    val dimension: T

    @get:JsonProperty(VALUE)
    val value: U
}
