package io.github.lengors.webscout.domain.utilities

import org.springframework.util.CollectionUtils
import org.springframework.util.MultiValueMap

fun <K, V> Map<K, V>.asMultiValueMap(): MultiValueMap<K, V> = CollectionUtils.toMultiValueMap(mapEachValue(::listOf))

inline fun <K, V, R> Map<out K, V>.mapEachValue(transform: (V) -> R): Map<K, R> = mapValues { transform(it.value) }
