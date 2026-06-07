package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.lengors.webscout.integrations.duckling.models.DucklingResponse.Properties.DIMENSION
import io.github.lengors.webscout.integrations.duckling.models.DucklingResponse.Properties.VALUE

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = DIMENSION,
    defaultImpl = DucklingAnyAmountOfMoneyLikeResponse::class,
)
@JsonSubTypes(
    JsonSubTypes.Type(value = DucklingAmountOfMoneyResponse::class, name = DucklingAmountOfMoneyDimension.VALUE),
    JsonSubTypes.Type(value = DucklingNumberResponse::class, name = DucklingNumberDimension.VALUE),
)
sealed interface DucklingAmountOfMoneyLikeResponse : DucklingResponse {
    @get:JsonProperty(DIMENSION)
    override val dimension: DucklingAmountOfMoneyLikeDimension

    @get:JsonProperty(VALUE)
    override val value: DucklingAmountOfMoneyLikeResponseValue
}
