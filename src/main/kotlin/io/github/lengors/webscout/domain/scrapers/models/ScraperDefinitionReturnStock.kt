package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnExtractStock
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnFlatStock
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnStock
import org.apache.commons.jexl3.JexlEngine

@JvmInline
value class ScraperDefinitionReturnStock private constructor(
    private val actions: List<ScraperDefinitionReturnStockAction>,
) : List<ScraperDefinitionReturnStockAction> by actions {
    constructor(
        specifications: List<ScraperSpecificationReturnStock>,
        jexlEngine: JexlEngine,
    ) : this(
        specifications.map {
            when (it) {
                is ScraperSpecificationReturnExtractStock -> ScraperDefinitionReturnExtractStockAction(it, jexlEngine)
                is ScraperSpecificationReturnFlatStock -> ScraperDefinitionReturnFlatStockAction(it, jexlEngine)
            }
        },
    )
}
