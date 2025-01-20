package io.github.lengors.webscout.domain.utilities

val Regex.Companion.decibels: Regex by lazy {
    Regex("(\\d+)\\s*([dD][bB])?")
}

val Regex.Companion.grading: Regex by lazy {
    Regex("[A-E1-5a-e]")
}

val Regex.Companion.noiseLevel: Regex by lazy {
    Regex("[A-C1-3a-c]")
}

val Regex.Companion.quantity: Regex by lazy {
    Regex("([><+\\-])?\\s*(\\d+)")
}

val Regex.Companion.supplementary: Regex by lazy {
    Regex("\\(.*\\)")
}

val Regex.Companion.whitespace: Regex by lazy {
    Regex("\\s+")
}
