package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationMapAction
import io.github.lengors.webscout.domain.jexl.services.createExpression
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionMapAction private constructor(
    override val maps: List<JexlExpression>,
) : ScraperDefinitionComputeAction {
    constructor(
        specification: ScraperSpecificationMapAction,
        jexlEngine: JexlEngine,
    ) : this(specification.maps.map(jexlEngine::createExpression))
}
