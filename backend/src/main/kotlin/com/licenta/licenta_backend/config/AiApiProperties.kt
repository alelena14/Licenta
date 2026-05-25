package com.licenta.licenta_backend.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ai")
data class AiApiProperties(
    var apiKey: String = ""
)