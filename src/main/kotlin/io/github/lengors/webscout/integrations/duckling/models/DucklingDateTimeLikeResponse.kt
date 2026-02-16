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
    defaultImpl = DucklingAnyDateTimeLikeResponse::class,
)
@JsonSubTypes(
    JsonSubTypes.Type(value = DucklingDateTimeResponse::class, name = DucklingDateTimeDimension.VALUE),
    JsonSubTypes.Type(value = DucklingDurationResponse::class, name = DucklingDurationDimension.VALUE),
)
sealed interface DucklingDateTimeLikeResponse : DucklingResponse {
    @get:JsonProperty(DIMENSION)
    override val dimension: DucklingDateTimeLikeDimension

    @get:JsonProperty(VALUE)
    override val value: DucklingDateTimeLikeResponseValue
}
