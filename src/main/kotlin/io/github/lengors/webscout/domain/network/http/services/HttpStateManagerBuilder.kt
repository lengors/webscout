package io.github.lengors.webscout.domain.network.http.services

import org.springframework.stereotype.Service

@Service
interface HttpStateManagerBuilder {
    fun build(): HttpStateManager
}
