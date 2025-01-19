package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZoneId
import java.util.Locale
import kotlin.reflect.KClass

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DucklingDateTimeRequest(
    @JsonProperty(DucklingRequest.TEXT)
    override val text: String,
    @JsonProperty(DucklingRequest.LOCALE)
    override val locale: Locale? = null,
    @JsonProperty(DucklingRequest.TIMEZONE)
    override val timezone: ZoneId? = null,
) : DucklingRequest<DucklingDateTimeDimension, DucklingDateTimeResponse> {
    @JsonIgnore
    override val dimension: DucklingDateTimeDimension = DucklingDateTimeDimension

    @JsonIgnore
    override val responseType: KClass<DucklingDateTimeResponse> = DucklingDateTimeResponse::class
}
