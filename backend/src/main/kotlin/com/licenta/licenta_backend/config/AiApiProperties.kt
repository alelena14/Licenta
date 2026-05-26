package com.licenta.licenta_backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ai")
data class AiApiProperties(
    @Value("\${ai.api-key}")
    private var apiKey: String = ""
)