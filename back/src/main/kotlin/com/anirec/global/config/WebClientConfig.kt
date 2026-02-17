package com.anirec.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(private val props: AppProperties) {

    @Bean
    fun jikanWebClient(): WebClient =
        WebClient.builder()
            .baseUrl(props.jikan.baseUrl)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs { it.defaultCodecs().maxInMemorySize(2 * 1024 * 1024) }
                    .build()
            )
            .build()
}
