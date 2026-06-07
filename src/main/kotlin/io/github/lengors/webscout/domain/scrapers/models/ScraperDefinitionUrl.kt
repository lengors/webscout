package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationUrl
import io.github.lengors.webscout.domain.jexl.services.createExpression
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionUrl private constructor(
    val location: JexlExpression? = null,
    val scheme: JexlExpression? = null,
    val host: JexlExpression? = null,
    val path: JexlExpression? = null,
    val parameters: Map<JexlExpression, List<JexlExpression>>? = null,
) {
    constructor(
        specification: ScraperSpecificationUrl,
        jexlEngine: JexlEngine,
    ) : this(
        specification.location?.let(jexlEngine::createExpression),
        specification.scheme?.let(jexlEngine::createExpression),
        specification.host?.let(jexlEngine::createExpression),
        specification.path?.let(jexlEngine::createExpression),
        specification.parameters
            ?.groupBy { it.name }
            ?.map {
                jexlEngine.createExpression(it.key) to
                    it.value.map { parameter ->
                        jexlEngine.createExpression(parameter.value)
                    }
            }?.toMap(),
    )
}
