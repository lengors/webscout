package io.github.lengors.webscout

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
@ConfigurationPropertiesScan
class WebscoutApplication

fun main(args: Array<String>) {
    runApplication<WebscoutApplication>(*args)
}
