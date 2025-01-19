package io.github.lengors.webscout.domain.validators

fun interface Validator<T> {
    fun isValid(input: T): Boolean
}
