package com.gamematch.api.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openApi(): OpenAPI = OpenAPI().info(
        Info()
            .title("GameMatch API")
            .version("1.0.0")
            .description("API do recomendador de jogos GameMatch (AP2 de Desenvolvimento Mobile)."),
    )
}
