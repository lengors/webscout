package io.github.lengors.webscout.domain.text.models

val Regex.Companion.decibels: Regex by lazy {
    Regex("(\\d+)\\s*([dD][bB])?")
}

val Regex.Companion.grading: Regex by lazy {
    Regex("[A-G1-7a-g]")
}

val Regex.Companion.noiseLevel: Regex by lazy {
    Regex("([^A-Za-z]+|^)(?<gradingletter>[A-Ca-c])([^A-Za-z]+|$)|([^0-9]+|^)(?<gradingnumber>[1-3])([^0-9]+|$)")
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
