package com.licenta.licenta_backend.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
class WebClientConfig {

    @Bean
    fun serpApiWebClient(): WebClient =
        WebClient.builder()
            .baseUrl("https://serpapi.com")
            .build()

    @Bean
    fun rapidApiWebClient(): WebClient {

        val httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(10))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
            .doOnConnected { conn ->
                conn
                    .addHandlerLast(ReadTimeoutHandler(10, TimeUnit.SECONDS))
                    .addHandlerLast(WriteTimeoutHandler(10, TimeUnit.SECONDS))
            }

        return WebClient.builder()
            .baseUrl("https://skincare-ingredient-analyzer.p.rapidapi.com")
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}
