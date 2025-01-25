package io.github.lengors.webscout.domain.events.models

import java.util.EventListener

interface EventListener<in T : Event> : EventListener {
    fun onEvent(event: T)
}
