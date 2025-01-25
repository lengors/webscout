package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.lengors.webscout.integrations.duckling.serializers.DucklingDimensionListSerializer
import java.time.ZoneId
import java.util.Locale
import kotlin.reflect.KClass

@JsonInclude(JsonInclude.Include.NON_NULL)
sealed interface DucklingRequest<T : DucklingDimension, U : DucklingResponse<T, *>> {
    companion object Properties {
        const val DIMENSIONS = "dims"
        const val LOCALE = "locale"
        const val TEXT = "text"
        const val TIMEZONE = "tz"
    }

    @get:JsonIgnore
    val dimension: T

    @get:JsonProperty(DIMENSIONS)
    @get:JsonSerialize(using = DucklingDimensionListSerializer::class)
    val dimensions: List<T>
        get() = listOf(dimension)

    @get:JsonProperty(LOCALE)
    val locale: Locale?

    @get:JsonIgnore
    val responseType: KClass<U>

    @get:JsonProperty(TEXT)
    val text: String

    @get:JsonProperty(TIMEZONE)
    val timezone: ZoneId?
}
