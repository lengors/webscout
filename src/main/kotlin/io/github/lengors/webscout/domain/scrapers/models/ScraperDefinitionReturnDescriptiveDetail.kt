package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnDescriptiveDetail
import io.github.lengors.webscout.domain.jexl.services.createExpression
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionReturnDescriptiveDetail private constructor(
    override val name: JexlExpression,
    override val description: JexlExpression,
    override val image: JexlExpression? = null,
) : ScraperDefinitionReturnDetail {
    constructor(
        specification: ScraperSpecificationReturnDescriptiveDetail,
        jexlEngine: JexlEngine,
    ) : this(
        jexlEngine.createExpression(specification.name),
        jexlEngine.createExpression(specification.description),
        specification.image?.let(jexlEngine::createExpression),
    )
}
