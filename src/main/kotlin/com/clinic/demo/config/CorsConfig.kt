package com.yourpackage.config   // پکیج خودتان را بگذارید

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {

    @Bean
    fun corsFilter(): CorsFilter {
        val config = CorsConfiguration().apply {
            // اجازه به فرانت Compose WasmJs
            allowedOrigins = listOf(
                "http://localhost:8080",
                "http://127.0.0.1:8080"
            )
            // اگر بعداً پورت فرانت عوض شد، اینجا اضافه کنید:
            // "http://localhost:3000"

            allowedMethods = listOf(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
            )

            allowedHeaders = listOf("*")
            exposedHeaders = listOf(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
            )

            allowCredentials = true
            maxAge = 3600L          // کش preflight به مدت ۱ ساعت
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }

        return CorsFilter(source)
    }
}