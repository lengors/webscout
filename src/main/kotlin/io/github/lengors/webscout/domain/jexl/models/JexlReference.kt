package io.github.lengors.webscout.domain.jexl.models

import io.github.lengors.webscout.domain.utilities.whitespace
import org.apache.commons.lang3.StringUtils

interface JexlReference<out T : Any> {
    fun at(index: Int): JexlReference<Any> =
        flatMap {
            when (it) {
                is List<*> -> it[index]
                is Iterable<*> -> it.toList()[index]
                else -> it.takeIf { index == 0 }
            }
        }

    val executionContext: JexlExecutionContext

    fun <U : Any> flatMap(action: (Any) -> U?): JexlReference<U> =
        valueOrNull.let {
            when (it) {
                is JexlReference<*> -> it.flatMap(action)
                else -> executionContext.makeReference(it?.let(action))
            }
        }

    fun <U : Any> map(action: (T) -> U?): JexlReference<U> = executionContext.makeReference(valueOrNull?.let(action))

    fun string(): JexlReference<String> =
        flatMap {
            when (it) {
                is String -> it
                else -> it.toString()
            }
        }

    fun text(): JexlReference<String> =
        mapString { stringValue ->
            stringValue
                .trim()
                .replace(Regex.whitespace, StringUtils.SPACE)
        }

    val valueOrNull: T?
}

fun <T : Any, U : Any> JexlReference<T>.mapString(action: (String) -> U?): JexlReference<U> = string().map(action)

val <T : Any> JexlReference<T>?.value: T
    get() = this?.valueOrNull ?: throw NoSuchElementException("No value exists")
