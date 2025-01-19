package io.github.lengors.webscout.integrations.duckling.models

data object DucklingAmountOfMoneyDimension : DucklingDimension {
    const val VALUE = "amount-of-money"

    override fun toString(): String = VALUE
}
