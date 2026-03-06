package io.github.lengors.webscout.domain.jexl.namespaces

import io.github.lengors.webscout.domain.jexl.services.JexlNamespace
import org.springframework.stereotype.Component
import java.net.URI
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension

@Component("resolver")
data object JexlResolverNamespace : JexlNamespace {
    fun name(value: String?): String? = value?.let { Path(value).nameWithoutExtension }

    fun resolve(
        uri: URI,
        path: URI,
    ): URI = uri.resolve(path)

    fun resolve(
        uri: URI,
        path: String,
    ): URI = uri.resolve(path)
}
