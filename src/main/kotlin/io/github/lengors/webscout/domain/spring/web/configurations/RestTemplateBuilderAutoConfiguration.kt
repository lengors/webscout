package io.github.lengors.webscout.domain.spring.web.configurations

import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class RestTemplateBuilderAutoConfiguration : RestTemplateAutoConfiguration()
