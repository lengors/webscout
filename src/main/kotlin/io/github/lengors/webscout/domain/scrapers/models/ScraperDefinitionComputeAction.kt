package io.github.lengors.webscout.domain.scrapers.models

import org.apache.commons.jexl3.JexlExpression

sealed interface ScraperDefinitionComputeAction : ScraperDefinitionAction {
    val maps: List<JexlExpression>
}
