package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnDescriptionlessDetail
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnDescriptiveDetail
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnDetail
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnFlatDetail
import org.apache.commons.jexl3.JexlEngine

@JvmInline
value class ScraperDefinitionReturnDetail private constructor(
    private val actions: List<ScraperDefinitionReturnDetailAction>,
) : List<ScraperDefinitionReturnDetailAction> by actions {
    constructor(
        specifications: List<ScraperSpecificationReturnDetail>,
        jexlEngine: JexlEngine,
    ) : this(
        specifications.map {
            when (it) {
                is ScraperSpecificationReturnDescriptiveDetail ->
                    ScraperDefinitionReturnDescriptiveDetailAction(it, jexlEngine)

                is ScraperSpecificationReturnDescriptionlessDetail ->
                    ScraperDefinitionReturnDescriptionlessDetailAction(it, jexlEngine)

                is ScraperSpecificationReturnFlatDetail ->
                    ScraperDefinitionReturnFlatDetailAction(it, jexlEngine)
            }
        },
    )
}
