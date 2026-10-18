package com.clinic.demo.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Health", description = "بررسی سلامت سرویس")
class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check")
    fun health() = mapOf(
        "status" to "ok",
        "service" to "psych-clinic",
        "timestamp" to System.currentTimeMillis()
    )

    @GetMapping("/ready")
    @Operation(summary = "Readiness probe")
    fun ready() = mapOf("ready" to true)

    @GetMapping("/live")
    @Operation(summary = "Liveness probe")
    fun live() = mapOf("alive" to true)
}