package io.github.lengors.webscout.integrations.duckling.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.type.CollectionLikeType
import com.fasterxml.jackson.databind.type.SimpleType
import io.github.lengors.webscout.integrations.duckling.models.DucklingDimension

object DucklingDimensionListSerializer : StdSerializer<List<DucklingDimension>>(
    CollectionLikeType.upgradeFrom(
        SimpleType.constructUnsafe(List::class.java),
        SimpleType.constructUnsafe(DucklingDimension::class.java),
    ),
) {
    private fun readResolve(): Any = DucklingDimensionListSerializer

    override fun serialize(
        value: List<DucklingDimension>?,
        generator: JsonGenerator?,
        provider: SerializerProvider?,
    ) {
        generator
            ?.codec
            ?.takeIf { it is ObjectMapper }
            ?.let { it as ObjectMapper }
            ?.also {
                value
                    ?.map(it::writeValueAsString)
                    ?.toString()
                    ?.also(generator::writeString)
            }
    }
}
