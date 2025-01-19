package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnFlatStock
import io.github.lengors.webscout.domain.jexl.services.createExpression
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionReturnFlatStockAction private constructor(
    val flattens: JexlExpression,
    val extracts: List<ScraperDefinitionReturnStockAction>,
) : ScraperDefinitionReturnStockAction {
    constructor(
        specification: ScraperSpecificationReturnFlatStock,
        jexlEngine: JexlEngine,
    ) : this(
        jexlEngine.createExpression(specification.flattens),
        ScraperDefinitionReturnStock(specification.extracts, jexlEngine),
    )
}
