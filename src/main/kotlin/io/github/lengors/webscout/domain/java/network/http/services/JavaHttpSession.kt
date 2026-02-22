package io.github.lengors.webscout.domain.java.network.http.services

import io.github.lengors.webscout.domain.network.http.services.HttpSession
import org.slf4j.LoggerFactory
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI

@ConsistentCopyVisibility
data class JavaHttpSession private constructor(
    private val cookieManager: CookieManager = CookieManager(),
    private val headers: MutableMap<String, String> = mutableMapOf(),
) : HttpSession {
    companion object {
        private val logger = LoggerFactory.getLogger(JavaHttpSession::class.java)
    }

    constructor() : this(headers = mutableMapOf())

    override fun getCookies(uri: URI): Map<String, String> = cookieManager.cookieStore[uri].associate {
        it.name to it.value
    }

    override fun getHeaders(): Map<String, String> = headers

    override fun setCookie(uri: URI, cookieName: String, cookieValue: String) {
        cookieManager.cookieStore.add(uri, HttpCookie(cookieName, cookieValue))
    }

    override fun setHeader(name: String, value: String) {
        headers[name] = value
    }

    override fun update(
        uri: URI,
        headers: Map<String, List<String>>
    ) {
        runCatching { cookieManager.put(uri, headers) }
            .onFailure { logger.error("Failed to store state", it) }
    }
}
