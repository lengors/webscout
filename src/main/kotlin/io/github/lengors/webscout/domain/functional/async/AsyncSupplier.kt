package io.github.lengors.webscout.domain.functional.async

import java.util.function.Function

data class AsyncSupplier<T>(
    private val asyncSupplier: suspend () -> T,
) : (suspend () -> T) by asyncSupplier {
    fun <U> flatMap(mapper: Function<T, AsyncSupplier<U>>): AsyncSupplier<U> =
        AsyncSupplier {
            mapper
                .apply(asyncSupplier())
                .asyncSupplier()
        }

    fun <U> map(mapper: Function<T, U>): AsyncSupplier<U> =
        AsyncSupplier {
            mapper.apply(asyncSupplier())
        }

    fun <U> map(mapper: suspend (T) -> U): AsyncSupplier<U> =
        AsyncSupplier {
            mapper(asyncSupplier())
        }
}
