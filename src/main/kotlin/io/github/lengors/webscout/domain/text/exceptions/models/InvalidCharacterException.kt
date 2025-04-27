package io.github.lengors.webscout.domain.text.exceptions.models

class InvalidCharacterException(
    character: Char,
) : Exception("Invalid character found: '$character'")
