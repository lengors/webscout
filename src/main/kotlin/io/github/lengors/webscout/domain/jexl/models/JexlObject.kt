package io.github.lengors.webscout.domain.jexl.models

data class JexlObject<out T : Any>(
    override val executionContext: JexlExecutionContext,
    override val valueOrNull: T? = null,
) : JexlReference<T>
