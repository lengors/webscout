package io.github.lengors.webscout.domain.jexl.services

import io.github.lengors.protoscout.domain.jexl.models.JexlExpressionSpecification
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlExpression

fun JexlEngine.createExpression(specification: JexlExpressionSpecification): JexlExpression = createExpression(specification.jexl)
