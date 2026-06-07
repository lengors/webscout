package io.github.lengors.webscout.domain.jexl.models

import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.internal.Closure
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

@JvmInline
value class JexlContextFunctionalAdapter(
    private val jexlContext: JexlContext,
) : JexlFunctionalAdapter {
    override fun asConsumer(closure: Closure): Consumer<*> =
        Consumer<Any?> {
            closure.variables
            closure.execute(jexlContext, it)
        }

    override fun asFunction(closure: Closure): java.util.function.Function<*, *> =
        Function<Any?, Any?> {
            closure.execute(jexlContext, it)
        }

    override fun asPredicate(closure: Closure): Predicate<*> =
        Predicate<Any?> {
            closure.execute(jexlContext, it) as Boolean
        }

    override fun asRunnable(closure: Closure): Runnable =
        Runnable {
            closure.execute(jexlContext)
        }

    override fun asSupplier(closure: Closure): Supplier<*> =
        Supplier {
            closure.execute(jexlContext)
        }

    override fun transform(
        value: Any?,
        closure: Closure,
    ): Any? = closure.execute(jexlContext, value)
}
