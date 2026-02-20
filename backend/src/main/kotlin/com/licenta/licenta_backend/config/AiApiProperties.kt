package com.licenta.licenta_backend.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ai")
data class AiApiProperties(
    var apiKey: String = ""
)