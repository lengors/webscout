package io.github.lengors.webscout.domain.jexl.namespaces

import io.github.lengors.webscout.domain.jexl.services.JexlNamespace
import org.springframework.stereotype.Component
import java.util.function.Function
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.asStream

@Component("collections")
data object JexlCollectionNamespace : JexlNamespace {
    fun <T> concat(
        left: Stream<out T>,
        right: Stream<out T>,
    ): Stream<T> = Stream.concat(left, right)

    fun <T> enumerate(iterable: Iterable<T>): List<Map.Entry<Int, T & Any>> = enumerate(iterable, 0)

    fun <T> enumerate(
        iterable: Iterable<T>,
        offset: Int,
    ): List<Map.Entry<Int, T & Any>> =
        iterable
            .filterNotNull()
            .mapIndexed { index, value -> java.util.Map.entry(index.plus(offset), value) }

    fun <T> listOf(vararg values: T): List<T> = values.toList()

    fun join(
        iterable: Iterable<*>,
        separator: String,
    ): String = iterable.joinToString(separator)

    fun <T> join(
        iterable: Iterable<T>,
        separator: String,
        transformer: Function<T, String>,
    ): String = iterable.joinToString(separator) { transformer.apply(it) }

    fun <T, U> map(
        iterable: Iterable<T>,
        transformer: Function<T, U>,
    ): List<U> = iterable.map { transformer.apply(it) }

    fun <K : Any, V : Any> mapCollector(): Collector<in Map.Entry<K, V>, *, Map<K, V>> =
        Collectors.toUnmodifiableMap(Map.Entry<K, V>::key, Map.Entry<K, V>::value)

    fun <T> stream(iterable: Iterable<T>): Stream<T> =
        iterable
            .asSequence()
            .asStream()

    fun <T> streamOf(value: T?): Stream<T> = Stream.ofNullable(value)
}
