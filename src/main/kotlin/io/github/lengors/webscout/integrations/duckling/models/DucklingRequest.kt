package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZoneId
import java.util.Locale
import kotlin.reflect.KClass

@JsonInclude(JsonInclude.Include.NON_NULL)
sealed interface DucklingRequest<T : DucklingResponse> {
    companion object Properties {
        const val LOCALE = "locale"
        const val TEXT = "text"
        const val TIMEZONE = "tz"
    }

    @get:JsonProperty(LOCALE)
    val locale: Locale?

    @get:JsonIgnore
    val responseType: KClass<T>

    @get:JsonProperty(TEXT)
    val text: String

    @get:JsonProperty(TIMEZONE)
    val timezone: ZoneId?
}
