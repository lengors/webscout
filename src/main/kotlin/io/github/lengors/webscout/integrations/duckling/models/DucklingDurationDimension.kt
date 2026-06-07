package io.github.lengors.webscout.integrations.duckling.models

data object DucklingDurationDimension : DucklingDateTimeLikeDimension {
    const val VALUE = "duration"

    override fun toString(): String = VALUE
}
