package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnDescriptionlessDetail
import io.github.lengors.webscout.domain.jexl.services.createExpression
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionReturnVisualDetail private constructor(
    override val name: JexlExpression,
    override val image: JexlExpression,
) : ScraperDefinitionReturnDetail {
    constructor(
        specification: ScraperSpecificationReturnDescriptionlessDetail,
        jexlEngine: JexlEngine,
    ) : this(
        jexlEngine.createExpression(specification.name),
        specification.image.let(jexlEngine::createExpression),
    )

    override val description: JexlExpression? = null
}
