package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import io.github.lengors.webscout.integrations.duckling.deserializers.DucklingDimensionDeserializer

@JsonSerialize(using = ToStringSerializer::class)
@JsonDeserialize(using = DucklingDimensionDeserializer::class)
sealed interface DucklingDimension {
    companion object {
        val values: Map<String, DucklingDimension> =
            setOf(
                DucklingAmountOfMoneyDimension,
                DucklingDateTimeDimension,
            ).associateBy(Any::toString)
    }
}
