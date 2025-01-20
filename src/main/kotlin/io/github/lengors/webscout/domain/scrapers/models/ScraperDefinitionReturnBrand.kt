package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnBrand
import io.github.lengors.webscout.domain.jexl.services.createExpression
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionReturnBrand private constructor(
    val description: JexlExpression,
    val image: JexlExpression? = null,
) {
    constructor(
        specification: ScraperSpecificationReturnBrand,
        jexlEngine: JexlEngine,
    ) : this(
        jexlEngine.createExpression(specification.description),
        specification.image?.let(jexlEngine::createExpression),
    )
}
