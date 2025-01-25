package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnExtractStock
import io.github.lengors.webscout.domain.jexl.services.createExpression
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionReturnExtractStockAction private constructor(
    val availability: JexlExpression,
    val storage: JexlExpression? = null,
    val deliveryDateTime: JexlExpression? = null,
) : ScraperDefinitionReturnStockAction {
    constructor(
        specification: ScraperSpecificationReturnExtractStock,
        jexlEngine: JexlEngine,
    ) : this(
        jexlEngine.createExpression(specification.availability),
        specification.storage?.let(jexlEngine::createExpression),
        specification.deliveryDateTime?.let(jexlEngine::createExpression),
    )
}
