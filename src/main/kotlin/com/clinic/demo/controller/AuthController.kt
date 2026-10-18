package com.clinic.demo.controller


import com.clinic.demo.dto.*
import com.clinic.demo.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "ثبت‌نام و ورود کاربران")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "ثبت‌نام کاربر جدید")
    fun register(@Valid @RequestBody req: RegisterRequest): AuthResponse =
        authService.register(req)

    @PostMapping("/login")
    @Operation(summary = "ورود و دریافت توکن JWT")
    fun login(@Valid @RequestBody req: LoginRequest): AuthResponse =
        authService.login(req)

    @GetMapping("/me")
    @Operation(summary = "اطلاعات کاربر جاری از روی توکن")
    fun me(@RequestHeader("Authorization") auth: String): Map<String, Any?> {
        val token = auth.removePrefix("Bearer ").trim()
        return mapOf(
            "userId" to authService.extractUserId(token),
            "role" to authService.extractRole(token)
        )
    }
}