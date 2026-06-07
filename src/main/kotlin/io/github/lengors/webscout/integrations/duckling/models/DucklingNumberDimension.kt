package io.github.lengors.webscout.integrations.duckling.models

data object DucklingNumberDimension : DucklingAmountOfMoneyLikeDimension {
    const val VALUE = "number"

    override fun toString(): String = VALUE
}
