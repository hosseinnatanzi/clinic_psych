package com.clinic.demo.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank val mobile: String,
    @field:NotBlank @field:Size(min = 6) val password: String,
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    val email: String? = null,
    val nationalCode: String? = null,
    val role: String = "PATIENT"
)

data class LoginRequest(
    @field:NotBlank val mobile: String,
    @field:NotBlank val password: String
)

data class AuthResponse(val token: String, val userId: Long, val role: String)