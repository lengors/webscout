package io.github.lengors.webscout.domain.converters

import org.springframework.stereotype.Service

@Service
interface ConverterDescriptor<I, O> {
    val readingConverterSupplier: ConverterSupplier<O, I>

    val writingConverterSupplier: ConverterSupplier<I, O>
}
