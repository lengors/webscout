package io.github.lengors.webscout.testing.duckling.containers

import io.github.lengors.webscout.integrations.duckling.properties.DucklingClientConnectionDetails
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource

class DucklingContainerConnectionDetailsFactory :
    ContainerConnectionDetailsFactory<DucklingContainer<*>, DucklingClientConnectionDetails>() {
    override fun getContainerConnectionDetails(
        source: ContainerConnectionSource<DucklingContainer<*>?>?,
    ): DucklingClientConnectionDetails? = source?.let(::DucklingContainerConnectionDetails)

    private class DucklingContainerConnectionDetails(
        source: ContainerConnectionSource<DucklingContainer<*>?>,
    ) : ContainerConnectionDetails<DucklingContainer<*>?>(source),
        DucklingClientConnectionDetails {
        override val url: String
            get() =
                container
                    ?.let { "http://${it.host}:${it.getMappedPort(DucklingContainer.DUCKLING_PORT)}" }
                    ?: throw IllegalStateException("Missing duckling container")
    }
}
