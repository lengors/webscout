package io.github.lengors.webscout

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebscoutApplication

fun main(args: Array<String>) {
    runApplication<WebscoutApplication>(*args)
}
