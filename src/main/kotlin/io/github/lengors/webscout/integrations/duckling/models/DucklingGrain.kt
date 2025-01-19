package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonProperty

enum class DucklingGrain {
    @JsonProperty("second")
    SECOND,

    @JsonProperty("minute")
    MINUTE,

    @JsonProperty("hour")
    HOUR,

    @JsonProperty("day")
    DAY,

    @JsonProperty("week")
    WEEK,

    @JsonProperty("month")
    MONTH,

    @JsonProperty("quarter")
    QUARTER,

    @JsonProperty("year")
    YEAR,
}
