package com.clinic.demo.dto


data class ErrorResponse(val error: String, val timestamp: Long = System.currentTimeMillis())
data class MessageResponse(
    val message: String,
    val ok: Boolean = true
)