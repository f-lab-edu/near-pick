package com.nearpick.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("NearPick API")
                    .description("NearPick API 명세")
                    .version("1.0.0")
            )
            .addSecurityItem(
                SecurityRequirement().addList("bearerAuth")
            )
            .components(
                Components().addSecuritySchemes("bearerAuth", createSecurityScheme())
            )
    }

    fun createSecurityScheme(): SecurityScheme? {
        return SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .bearerFormat("JWT")
            .scheme("bearer")
            .name("Authorization")
    }
}
