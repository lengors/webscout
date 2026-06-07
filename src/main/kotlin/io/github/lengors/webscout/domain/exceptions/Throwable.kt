package io.github.lengors.webscout.domain.exceptions

import kotlin.reflect.KClass

fun <T : Any> Throwable.findInStackTrace(type: Class<T>): T? = if (type.isInstance(this)) type.cast(this) else cause?.findInStackTrace(type)

fun <T : Any> Throwable.findInStackTrace(type: KClass<T>): T? = findInStackTrace(type.java)

inline fun <reified T : Any> Throwable.findInStackTrace(): T? = findInStackTrace(T::class)
