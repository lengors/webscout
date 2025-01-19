package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationDataPayload
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationJsonPayload
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationPayload
import io.github.lengors.webscout.domain.jexl.services.createExpression
import io.github.lengors.webscout.domain.utilities.mapEachValue
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionPayload private constructor(
    val type: ScraperDefinitionPayloadType,
    val fields: Map<String, JexlExpression> = emptyMap(),
) {
    constructor(
        specification: ScraperSpecificationPayload,
        jexlEngine: JexlEngine,
    ) : this(
        when (specification) {
            is ScraperSpecificationDataPayload -> ScraperDefinitionPayloadType.DATA
            is ScraperSpecificationJsonPayload -> ScraperDefinitionPayloadType.JSON
        },
        when (specification) {
            is ScraperSpecificationDataPayload -> specification.data.additionalProperties
            is ScraperSpecificationJsonPayload -> specification.json.additionalProperties
        }.mapEachValue(jexlEngine::createExpression),
    )
}
