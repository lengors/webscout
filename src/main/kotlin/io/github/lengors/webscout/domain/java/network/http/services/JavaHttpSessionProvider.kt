package io.github.lengors.webscout.domain.java.network.http.services

import io.github.lengors.webscout.domain.network.http.services.HttpSession
import io.github.lengors.webscout.domain.network.http.services.HttpSessionProvider

class JavaHttpSessionProvider : HttpSessionProvider {
    override fun provide(): HttpSession = JavaHttpSession()
}
