package io.github.lengors.webscout.domain.validators

import org.apache.commons.validator.routines.EmailValidator

data object EmailValidator : Validator<String> {
    private val emailValidator: EmailValidator = EmailValidator.getInstance()

    override fun isValid(input: String): Boolean = emailValidator.isValid(input)
}
