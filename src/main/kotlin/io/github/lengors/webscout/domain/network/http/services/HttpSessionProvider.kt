package io.github.lengors.webscout.domain.network.http.services

interface HttpSessionProvider {
    fun provide(): HttpSession
}
