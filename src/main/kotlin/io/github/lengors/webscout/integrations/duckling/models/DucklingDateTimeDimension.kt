package io.github.lengors.webscout.integrations.duckling.models

data object DucklingDateTimeDimension : DucklingDateTimeLikeDimension {
    const val VALUE = "time"

    override fun toString(): String = VALUE
}
