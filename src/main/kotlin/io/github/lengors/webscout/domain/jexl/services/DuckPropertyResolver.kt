package io.github.lengors.webscout.domain.jexl.services

import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.introspection.JexlPropertyGet
import org.apache.commons.jexl3.introspection.JexlPropertySet
import org.apache.commons.jexl3.introspection.JexlUberspect

object DuckPropertyResolver : JexlUberspect.PropertyResolver {
    private val delegatingResolvers = listOf(JexlUberspect.JexlResolver.DUCK)

    override fun getPropertyGet(
        uberspect: JexlUberspect?,
        `object`: Any?,
        identifier: Any?,
    ): JexlPropertyGet? =
        uberspect
            ?.takeUnless { `object` == null || `object` is JexlContext }
            ?.getPropertyGet(delegatingResolvers, `object`, identifier)

    override fun getPropertySet(
        uberspect: JexlUberspect?,
        `object`: Any?,
        identifier: Any?,
        argument: Any?,
    ): JexlPropertySet? =
        uberspect
            ?.takeUnless { `object` == null || `object` is JexlContext }
            ?.getPropertySet(delegatingResolvers, `object`, identifier, argument)
}
