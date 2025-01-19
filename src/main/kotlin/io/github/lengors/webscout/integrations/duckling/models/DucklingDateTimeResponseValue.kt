package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes(
    JsonSubTypes.Type(DucklingDateTimeInstantResponseValue::class),
    JsonSubTypes.Type(DucklingDateTimeRangeResponseValue::class),
)
sealed interface DucklingDateTimeResponseValue : DucklingResponseValue
