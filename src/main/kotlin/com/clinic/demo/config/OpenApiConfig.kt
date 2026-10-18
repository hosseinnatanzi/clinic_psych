package com.clinic.demo.config

 
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI = OpenAPI()
        .info(Info()
            .title("Psychology Clinic API")
            .description("REST API for the psychology clinic management system")
            .version("1.0.0"))
        .addSecurityItem(SecurityRequirement().addList("bearerAuth"))
        .components(Components().addSecuritySchemes("bearerAuth",
            SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
}