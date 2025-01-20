package io.github.lengors.webscout.integrations.duckling.models

data object DucklingDateTimeDimension : DucklingDimension {
    const val VALUE = "time"

    override fun toString(): String = VALUE
}
