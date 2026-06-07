package io.github.lengors.webscout.testing.duckling.containers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class DucklingContainer<T : DucklingContainer<T>>(
    dockerImageName: DockerImageName,
) : GenericContainer<T>(dockerImageName) {
    companion object {
        const val DUCKLING_PORT = 8000
    }

    init {
        addExposedPort(DUCKLING_PORT)
    }

    constructor(dockerImageName: String) : this(DockerImageName.parse(dockerImageName))
}
