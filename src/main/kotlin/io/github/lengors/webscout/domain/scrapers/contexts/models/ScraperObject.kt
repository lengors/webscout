package io.github.lengors.webscout.domain.scrapers.contexts.models

data class ScraperObject<out T : Any>(
    override val executionContext: ScraperExecutionContext,
    override val valueOrNull: T? = null,
) : ScraperReference<T>
