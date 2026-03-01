package io.github.lengors.webscout.domain.scrapers.services

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.CaffeineSpec
import io.github.lengors.protoscout.domain.scrapers.specifications.models.ScraperSpecification
import io.github.lengors.webscout.domain.network.http.services.HttpSession
import io.github.lengors.webscout.domain.network.http.services.HttpSessionProvider
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.cache.CacheProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnMissingBean(
    value = [ScraperHttpSessionManager::class],
    ignored = [CaffeineCachingScraperHttpSessionManager::class],
)
class CaffeineCachingScraperHttpSessionManager(
    cacheProperties: CacheProperties,
    private val httpSessionProvider: HttpSessionProvider,
) : ScraperHttpSessionManager {
    companion object {
        private val logger = LoggerFactory.getLogger(CaffeineCachingScraperHttpSessionManager::class.java)
    }

    private val httpSessionCaches =
        Caffeine
            .newBuilder()
            .build<String, Cache<Map<String, String>, HttpSession>>()
    private val cacheSpec =
        cacheProperties.caffeine
            ?.spec
            ?.takeIf(String::isNotBlank)
            ?.let(CaffeineSpec::parse)
    private val cacheBuilder
        get() =
            cacheSpec
                ?.let { Caffeine.from(it) }
                ?: Caffeine.newBuilder()

    override fun discardHttpSession(specification: ScraperSpecification) {
        httpSessionCaches
            .invalidate(specification.name)
            .also { logger.info("Discarding http sessions for ScraperSpecification(name={})", specification.name) }
    }

    override fun getHttpSession(
        specification: ScraperSpecification,
        inputs: Map<String, String>,
    ): HttpSession =
        httpSessionCaches
            .get(specification.name) {
                cacheBuilder
                    .evictionListener { _: Map<String, String>?, _: HttpSession?, _ ->
                        httpSessionCaches
                            .asMap()
                            .computeIfPresent(specification.name) { _, cache ->
                                cache.takeIf { it.estimatedSize() > 0 }
                            }
                    }.build()
            }.get(inputs) { httpSessionProvider.provide() }
}
