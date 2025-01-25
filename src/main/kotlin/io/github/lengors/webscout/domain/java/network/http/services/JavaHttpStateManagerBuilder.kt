package io.github.lengors.webscout.domain.java.network.http.services

import io.github.lengors.webscout.domain.network.http.services.HttpStateManager
import io.github.lengors.webscout.domain.network.http.services.HttpStateManagerBuilder

class JavaHttpStateManagerBuilder : HttpStateManagerBuilder {
    override fun build(): HttpStateManager = JavaHttpStateManager()
}
