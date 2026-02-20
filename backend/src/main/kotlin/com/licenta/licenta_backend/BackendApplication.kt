package com.licenta.licenta_backend

import com.licenta.licenta_backend.config.RapidApiProperties
import com.licenta.licenta_backend.config.SerpApiProperties
import com.licenta.licenta_backend.config.AiApiProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(
	SerpApiProperties::class,
	RapidApiProperties::class,
	AiApiProperties::class
)
class LicenceBackendApplication

fun main(args: Array<String>) {
	runApplication<LicenceBackendApplication>(*args)
}
