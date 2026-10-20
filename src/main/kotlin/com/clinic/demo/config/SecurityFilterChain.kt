package com.yourpackage.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { }                    // فعال‌سازی CORS
            .csrf { it.disable() }       // برای API معمولاً CSRF را خاموش می‌کنیم
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // اگر JWT دارید
            }
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/api/auth/**",
                        "/api/public/**",
                        "/error"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
        // اگر JWT دارید، فیلتر JWT را اینجا اضافه کنید

        return http.build()
    }

    // این Bean را هم می‌توانید داشته باشید (اختیاری اگر CorsFilter جدا دارید)
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = listOf(
                "http://localhost:8080",
                "http://127.0.0.1:8080"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization", "Content-Type")
            allowCredentials = true
            maxAge = 3600L
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }
}