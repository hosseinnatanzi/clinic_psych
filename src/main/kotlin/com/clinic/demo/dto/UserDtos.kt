package com.clinic.demo.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserResponse(
    val id: Long, val mobile: String,
    val firstName: String, val lastName: String,
    val email: String?, val role: String, val isActive: Boolean,
    val createdAt: String? = null
)

data class DoctorSummary(
    val id: Long, val name: String,
    val specialty: String?, val consultationFee: String? = null
)

data class UpdateUserRequest(
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    @field:Email val email: String? = null,
    val nationalCode: String? = null
)

data class ChangePasswordRequest(
    @field:NotBlank val currentPassword: String,
    @field:NotBlank @field:Size(min = 6) val newPassword: String
)

data class UpdateDoctorProfileRequest(
    val specialty: String? = null,
    val bio: String? = null,
    val licenseNumber: String? = null,
    val consultationFee: String? = null
)