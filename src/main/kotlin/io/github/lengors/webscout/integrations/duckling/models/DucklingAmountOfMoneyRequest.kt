package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZoneId
import java.util.Locale
import kotlin.reflect.KClass

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DucklingAmountOfMoneyRequest(
    @JsonProperty(DucklingRequest.TEXT)
    override val text: String,
    @JsonProperty(DucklingRequest.LOCALE)
    override val locale: Locale? = null,
    @JsonProperty(DucklingRequest.TIMEZONE)
    override val timezone: ZoneId? = null,
) : DucklingRequest<DucklingAmountOfMoneyDimension, DucklingAmountOfMoneyResponse> {
    @JsonIgnore
    override val dimension: DucklingAmountOfMoneyDimension = DucklingAmountOfMoneyDimension

    @JsonIgnore
    override val responseType: KClass<DucklingAmountOfMoneyResponse> = DucklingAmountOfMoneyResponse::class
}
