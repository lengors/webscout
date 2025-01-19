package io.github.lengors.webscout.domain.jexl.models

import io.github.lengors.webscout.domain.utilities.mapEachValue
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.JexlExpression
import kotlin.reflect.KClass
import kotlin.reflect.cast

interface JexlExecutionContext :
    JexlContext,
    JexlReference<Any> {
    fun <T : Any> JexlExpression?.compute(type: KClass<T>): JexlReference<T> =
        this
            ?.evaluate(this@JexlExecutionContext)
            .let {
                makeReference(
                    when (it) {
                        is JexlReference<*> -> it.valueOrNull
                        else -> it
                    }?.let(type::cast),
                )
            }

    fun JexlExpression?.compute(): JexlReference<Any> = compute(Any::class)

    fun <T : Any> Iterable<JexlExpression>?.compute(type: KClass<T>): List<JexlReference<T>> =
        this
            ?.map { it.compute(type) }
            ?: emptyList()

    fun Iterable<JexlExpression>?.compute(): List<JexlReference<Any>> = compute(Any::class)

    fun <T : Any> Map<String, JexlExpression>?.compute(type: KClass<T>): Map<String, JexlReference<T>> =
        this
            ?.mapEachValue { it.compute(type) }
            ?: emptyMap()

    fun Map<String, JexlExpression>?.compute(): Map<String, JexlReference<Any>> = compute(Any::class)

    override val executionContext: JexlExecutionContext
        get() = this

    fun <T : Any> makeReference(valueOrNull: T? = null): JexlReference<T> = JexlObject(this, valueOrNull)
}
