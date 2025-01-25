package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequestAction
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationRequestParser
import io.github.lengors.webscout.domain.jexl.services.createExpression
import io.github.lengors.webscout.domain.network.http.models.HttpMethod
import io.github.lengors.webscout.domain.utilities.mapEachValue
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionRequestAction private constructor(
    val url: ScraperDefinitionUrl,
    val method: HttpMethod,
    val parser: ScraperSpecificationRequestParser,
    val payload: ScraperDefinitionPayload? = null,
    val headers: Map<String, JexlExpression> = emptyMap(),
    override val maps: List<JexlExpression> = emptyList(),
) : ScraperDefinitionComputeAction {
    constructor(
        specification: ScraperSpecificationRequestAction,
        jexlEngine: JexlEngine,
    ) : this(
        ScraperDefinitionUrl(specification.requests.url, jexlEngine),
        HttpMethod.valueOf(
            specification.requests.method.name
                .uppercase(),
        ),
        specification.requests.parser,
        specification.requests.payload?.let { ScraperDefinitionPayload(it, jexlEngine) },
        specification.requests.headers
            ?.additionalProperties
            ?.mapEachValue(jexlEngine::createExpression)
            ?: emptyMap(),
        specification.maps?.map(jexlEngine::createExpression) ?: emptyList(),
    )
}
