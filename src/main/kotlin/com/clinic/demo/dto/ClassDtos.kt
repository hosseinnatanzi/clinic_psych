package com.clinic.demo.dto

data class CreateClassRequest(
    val title: String, val description: String? = null,
    val instructorId: Long? = null,
    val startAt: String, val endAt: String,
    val capacity: Int = 10, val price: String = "0"
)

data class UpdateClassRequest(
    val title: String? = null, val description: String? = null,
    val capacity: Int? = null, val price: String? = null,
    val isActive: Boolean? = null
)

data class RegisterClassRequest(val classId: Long)

data class ClassResponse(
    val id: Long, val title: String, val description: String? = null,
    val capacity: Int, val price: String, val startAt: String, val endAt: String,
    val isActive: Boolean = true, val registeredCount: Long = 0
)

data class ClassRegistrationResponse(
    val id: Long, val classId: Long, val classTitle: String,
    val patientId: Long, val patientName: String,
    val status: String, val registeredAt: String
)