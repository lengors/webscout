package io.github.lengors.webscout.domain.jexl.models

interface JexlSessionAdapter {
    fun getCookie(cookieName: String): String?

    fun setCookie(
        cookieName: String,
        cookieValue: String,
    )
}
