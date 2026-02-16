package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationDataPayload
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationJsonPayload
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationPayload
import org.apache.commons.jexl3.JexlEngine

@ConsistentCopyVisibility
data class ScraperDefinitionPayload private constructor(
    val type: ScraperDefinitionPayloadType,
    val fields: List<ScraperSpecificationPayloadField> = emptyList(),
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
            is ScraperSpecificationDataPayload -> specification.data
            is ScraperSpecificationJsonPayload -> specification.json
        }.map { ScraperSpecificationPayloadField(it, jexlEngine) },
    )
}
