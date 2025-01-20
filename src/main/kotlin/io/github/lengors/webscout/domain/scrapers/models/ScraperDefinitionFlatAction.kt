package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationFlatAction
import io.github.lengors.webscout.domain.jexl.services.createExpression
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionFlatAction private constructor(
    val flattens: JexlExpression,
) : ScraperDefinitionAction {
    constructor(
        specification: ScraperSpecificationFlatAction,
        jexlEngine: JexlEngine,
    ) : this(jexlEngine.createExpression(specification.flattens))
}
