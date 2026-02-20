package com.licenta.licenta_backend.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "serpapi")
data class SerpApiProperties(
    var apiKey: String = ""
)
