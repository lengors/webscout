package io.github.lengors.webscout.domain.jexl.models

import io.github.lengors.webscout.domain.network.http.services.HttpSession
import java.net.URI

data class JexlHttpSessionAdapter(
    private val httpSession: HttpSession,
    private val uri: URI,
) : JexlSessionAdapter {
    override fun getCookie(cookieName: String): String? = httpSession.getCookie(uri, cookieName)

    override fun setCookie(
        cookieName: String,
        cookieValue: String,
    ) {
        httpSession.setCookie(uri, cookieName, cookieValue)
    }
}
