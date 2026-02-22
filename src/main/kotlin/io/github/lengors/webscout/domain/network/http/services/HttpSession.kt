package io.github.lengors.webscout.domain.network.http.services

import java.net.URI

interface HttpSession {
    fun getHeader(name: String): String? = getHeaders()[name]

    fun getCookie(uri: URI, name: String): String? = getCookies(uri)[name]

    fun getCookies(uri: URI): Map<String, String>

    fun getHeaders(): Map<String, String>

    fun setCookie(uri: URI, cookieName: String, cookieValue: String)

    fun setHeader(name: String, value: String)

    fun update(uri: URI, headers: Map<String, List<String>>)
}
