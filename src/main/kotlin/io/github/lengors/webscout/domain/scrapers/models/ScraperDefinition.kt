package io.github.lengors.webscout.domain.scrapers.models

import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.jexl.services.createExpression
import io.github.lengors.webscout.domain.network.ssl.models.SslMaterial
import io.github.lengors.webscout.domain.network.ssl.services.SslMaterialLoader
import io.github.lengors.webscout.domain.network.ssl.services.loadCertificates
import io.github.lengors.webscout.domain.utilities.mapEachValue
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

@ConsistentCopyVisibility
data class ScraperDefinition private constructor(
    val name: String,
    val locale: JexlExpression,
    val timezone: JexlExpression,
    val defaultUrl: ScraperDefinitionUrl,
    val defaultHeaders: Map<String, JexlExpression> = emptyMap(),
    val defaultGates: List<JexlExpression> = emptyList(),
    val handlers: List<ScraperDefinitionHandler> = emptyList(),
    val requirements: List<ScraperDefinitionRequirement> = emptyList(),
    val certificates: SslMaterial? = null,
) {
    constructor(
        specification: ScraperSpecification,
        jexlEngine: JexlEngine,
        sslMaterialLoader: SslMaterialLoader,
    ) : this(
        specification.name,
        jexlEngine.createExpression(specification.settings.locale),
        jexlEngine.createExpression(specification.settings.timezone),
        ScraperDefinitionUrl(specification.settings.defaults.url, jexlEngine),
        specification.settings.defaults.headers
            ?.additionalProperties
            ?.mapEachValue(jexlEngine::createExpression)
            ?: emptyMap(),
        specification.settings.defaults.gates
            .map(jexlEngine::createExpression),
        specification.handlers.map { ScraperDefinitionHandler(it, jexlEngine) },
        specification.settings.requirements?.map(::ScraperDefinitionRequirement) ?: emptyList(),
        sslMaterialLoader.loadCertificates(specification.settings.certificates),
    )
}
