package io.github.lengors.webscout.domain.scrapers.models

import org.apache.commons.jexl3.JexlExpression

sealed interface ScraperDefinitionReturnExtractDetailAction : ScraperDefinitionReturnDetailAction {
    val name: JexlExpression

    val description: JexlExpression?

    val image: JexlExpression?
}
