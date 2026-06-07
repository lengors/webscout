package io.github.lengors.webscout.domain.jexl.models

import io.github.lengors.webscout.domain.functional.async.AsyncSupplier
import org.apache.commons.jexl3.internal.Closure
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

interface JexlFunctionalAdapter {
    fun asConsumer(closure: Closure): Consumer<*>

    fun asFunction(closure: Closure): Function<*, *>

    fun asPredicate(closure: Closure): Predicate<*>

    fun asRunnable(closure: Closure): Runnable

    fun asSupplier(closure: Closure): Supplier<*>

    fun <T> asyncSupplierOf(value: T): AsyncSupplier<T> = AsyncSupplier { value }

    fun transform(
        value: Any?,
        closure: Closure,
    ): Any?
}
