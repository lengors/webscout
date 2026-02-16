package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnFlatDetail
import io.github.lengors.webscout.domain.jexl.services.createExpression
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionReturnFlatDetailAction private constructor(
    val flattens: List<JexlExpression>,
    val extracts: ScraperDefinitionReturnDetail,
) : ScraperDefinitionReturnDetailAction {
    constructor(
        specification: ScraperSpecificationReturnFlatDetail,
        jexlEngine: JexlEngine,
    ) : this(
        specification.flattens.map(jexlEngine::createExpression),
        ScraperDefinitionReturnDetail(specification.extracts, jexlEngine),
    )
}
