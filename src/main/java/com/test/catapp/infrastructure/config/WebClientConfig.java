package com.test.catapp.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${catapi.base-url}")
    private String catApiBaseUrl;

    @Value("${catapi.api-key}")
    private String catApiKey;

    @Bean
    public WebClient catWebClient() {
        return WebClient.builder()
                .baseUrl(catApiBaseUrl)
                .defaultHeader("x-api-key", catApiKey)
                .build();
    }
}
