package io.github.lengors.webscout.integrations.duckling.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class DucklingAnyDateTimeLikeResponseValue : DucklingDateTimeLikeResponseValue
