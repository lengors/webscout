package io.github.lengors.webscout.integrations.duckling.properties

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails

interface DucklingClientConnectionDetails : ConnectionDetails {
    val url: String
}
