package com.licenta.licenta_backend.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "rapidapi")
data class RapidApiProperties(
    var apiKey: String = "",
    var host: String = ""
)
