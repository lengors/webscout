package io.github.lengors.webscout.domain.jexl.models

import io.github.lengors.webscout.domain.utilities.mapEachValue
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlExpression
import kotlin.reflect.KClass
import kotlin.reflect.cast

interface JexlExecutionContext : JexlContext {
    fun <T : Any> JexlExpression?.compute(type: KClass<T>): T? =
        this
            ?.evaluate(this@JexlExecutionContext)
            ?.let(type::cast)

    fun JexlExpression?.compute(): Any? = compute(Any::class)

    fun <T : Any> Iterable<JexlExpression>?.compute(type: KClass<T>): List<T?> =
        this
            ?.map { it.compute(type) }
            ?: emptyList()

    fun Iterable<JexlExpression>?.compute(): List<Any?> = compute(Any::class)

    fun <T : Any> Map<String, JexlExpression>?.compute(type: KClass<T>): Map<String, T?> =
        this
            ?.mapEachValue { it.compute(type) }
            ?: emptyMap()

    fun Map<String, JexlExpression>?.compute(): Map<String, Any?> = compute(Any::class)
}
