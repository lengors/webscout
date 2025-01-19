package io.github.lengors.webscout.integrations.duckling.deserializers

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
import io.github.lengors.webscout.integrations.duckling.models.DucklingDimension

object DucklingDimensionDeserializer : FromStringDeserializer<DucklingDimension>(DucklingDimension::class.java) {
    private fun readResolve(): Any = DucklingDimensionDeserializer

    override fun _deserialize(
        value: String?,
        context: DeserializationContext?,
    ): DucklingDimension =
        value
            ?.let { DucklingDimension.values[it] }
            ?: throw IllegalArgumentException("Value isn't a valid duckling dimension: $value")
}
