package io.github.lengors.webscout.domain.jexl.utilities

import io.github.lengors.webscout.domain.jexl.models.JexlReference

interface JexlStringUtilities {
    fun join(
        delimiter: String,
        vararg values: Any?,
    ): String =
        values
            .mapNotNull {
                when (it) {
                    is JexlReference<*> -> it.string().valueOrNull
                    else -> it?.toString()
                }
            }.joinToString(delimiter)
}
