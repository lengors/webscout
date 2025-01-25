package io.github.lengors.webscout.domain.utilities

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

val Dispatchers.VirtualThreadPerTaskExecutor by lazy {
    Executors
        .newVirtualThreadPerTaskExecutor()
        .asCoroutineDispatcher()
}
