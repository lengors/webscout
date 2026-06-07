package io.github.lengors.webscout.integrations.duckling.properties

@JvmInline
value class DucklingClientConnectionDetailsProperties(
    private val ducklingClientProperties: DucklingClientProperties,
) : DucklingClientConnectionDetails {
    override val url: String
        get() = ducklingClientProperties.url
}
