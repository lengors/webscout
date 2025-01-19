package io.github.lengors.webscout.domain.utilities

import kotlinx.coroutines.channels.SendChannel
import org.slf4j.Logger

suspend fun <T, U> SendChannel<U>.runCatching(
    logger: Logger,
    exceptionHandler: (Throwable) -> U,
    action: suspend () -> T,
): T? =
    runCatching { action() }
        .onFailure { throwable ->
            exceptionHandler(throwable)
                .also { logger.error("Emitted response error: {}", it, throwable) }
                .let { send(it) }
        }.getOrNull()
