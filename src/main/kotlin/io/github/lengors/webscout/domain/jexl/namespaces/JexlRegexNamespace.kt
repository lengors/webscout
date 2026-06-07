package io.github.lengors.webscout.domain.jexl.namespaces

import io.github.lengors.webscout.domain.jexl.services.JexlNamespace
import org.springframework.stereotype.Component

@Component("regex")
data object JexlRegexNamespace : JexlNamespace {
    fun match(
        value: String?,
        regex: String,
    ): String? = match(value, regex, null)

    fun match(
        value: String?,
        regex: String,
        group: Int?,
    ): String? =
        value?.let { src ->
            regex
                .toRegex(setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
                .find(src)
                ?.let { match ->
                    group
                        ?.let { match.groups[it]?.value }
                        ?: match.value
                }
        }
}
