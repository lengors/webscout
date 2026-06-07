package io.github.lengors.webscout.domain.converters

import org.springframework.core.convert.converter.Converter

typealias ConverterSupplier<S, T> = () -> Converter<S, T>
