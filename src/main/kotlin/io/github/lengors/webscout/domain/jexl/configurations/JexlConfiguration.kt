package io.github.lengors.webscout.domain.jexl.configurations

import io.github.lengors.webscout.domain.jexl.services.JexlNamespace
import io.github.lengors.webscout.domain.jexl.services.JexlStrategy
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlFeatures
import org.apache.commons.jexl3.introspection.JexlPermissions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class JexlConfiguration {
    @Bean
    fun builder(
        features: JexlFeatures,
        permissions: JexlPermissions,
        namespaces: Map<String, JexlNamespace>,
    ): JexlBuilder =
        JexlBuilder()
            .features(features)
            .namespaces(namespaces)
            .permissions(permissions)
            .strict(true)
            .silent(false)
            .safe(false)
            .strategy(JexlStrategy)

    @Bean
    fun engine(builder: JexlBuilder): JexlEngine = builder.create()

    @Bean
    fun features(): JexlFeatures =
        JexlFeatures
            .createDefault()
            .annotation(false)
            .importPragma(false)
            .loops(false)
            .namespacePragma(false)
            .newInstance(false)
            .pragma(false)
            .pragmaAnywhere(false)
            .sideEffect(false)
            .sideEffectGlobal(false)

    @Bean
    fun permissions(): JexlPermissions =
        JexlPermissions.RESTRICTED
            .compose("java.time.*")
            .compose("javax.money.*")
            .compose("org.springframework.util.*")
            .compose("org.springframework.web.util.*")
            .compose("io.github.lengors.webscout.domain.functional.async.*")
            .compose("io.github.lengors.webscout.domain.scrapers.contexts.models.*")
            .compose("io.github.lengors.webscout.domain.jexl.models.*")
            .compose("org.jsoup.nodes.*")
            .compose("com.fasterxml.jackson.databind.*")
}
