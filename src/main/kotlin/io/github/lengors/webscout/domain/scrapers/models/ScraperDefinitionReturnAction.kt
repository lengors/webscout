package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnAction
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnDescriptionlessDetail
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecificationReturnDescriptiveDetail
import io.github.lengors.webscout.domain.jexl.services.createExpression
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinitionReturnAction private constructor(
    val description: JexlExpression,
    val brand: ScraperDefinitionReturnBrand,
    val price: JexlExpression,
    val image: JexlExpression? = null,
    val stocks: List<ScraperDefinitionReturnStockAction> = emptyList(),
    val grip: JexlExpression? = null,
    val noise: JexlExpression? = null,
    val decibels: JexlExpression? = null,
    val consumption: JexlExpression? = null,
    val details: List<ScraperDefinitionReturnDetail> = emptyList(),
) : ScraperDefinitionAction {
    constructor(
        specification: ScraperSpecificationReturnAction,
        jexlEngine: JexlEngine,
    ) : this(
        jexlEngine.createExpression(specification.returns.description),
        ScraperDefinitionReturnBrand(specification.returns.brand, jexlEngine),
        jexlEngine.createExpression(specification.returns.price),
        specification.returns.image?.let(jexlEngine::createExpression),
        specification.returns.stocks?.let { ScraperDefinitionReturnStock(it, jexlEngine) } ?: emptyList(),
        specification.returns.grip?.let(jexlEngine::createExpression),
        specification.returns.noise?.let(jexlEngine::createExpression),
        specification.returns.decibels?.let(jexlEngine::createExpression),
        specification.returns.consumption?.let(jexlEngine::createExpression),
        specification.returns.details?.map {
            when (it) {
                is ScraperSpecificationReturnDescriptiveDetail ->
                    ScraperDefinitionReturnDescriptiveDetail(
                        it,
                        jexlEngine,
                    )

                is ScraperSpecificationReturnDescriptionlessDetail ->
                    ScraperDefinitionReturnVisualDetail(
                        it,
                        jexlEngine,
                    )
            }
        } ?: emptyList(),
    )
}
