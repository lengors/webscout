package io.github.lengors.webscout.domain.observation

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.Observation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.use

suspend fun <T> Observation.suspendableObserve(block: suspend CoroutineScope.() -> T): T {
    start()
    return withContext(openScope().use { observationRegistry.asContextElement() }) {
        try {
            block()
        } catch (throwable: Throwable) {
            error(throwable)
            throw throwable
        } finally {
            stop()
        }
    }
}
