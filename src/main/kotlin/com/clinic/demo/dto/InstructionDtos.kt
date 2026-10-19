package com.clinic.demo.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateInstructionRequest(
    @field:NotBlank @field:Size(max = 200) val title: String,
    @field:NotBlank val content: String,
    val patientId: Long? = null,
    val appointmentId: Long? = null,
    val assignedToId: Long? = null,
    val priority: String = "NORMAL",
    val dueAt: String? = null
)

data class UpdateInstructionRequest(
    val status: String? = null,
    val assignedToId: Long? = null,
    val completionNote: String? = null,
    val priority: String? = null
)

data class InstructionResponse(
    val id: Long,
    val title: String,
    val content: String,
    val status: String,
    val priority: String,
    val doctorId: Long,
    val doctorName: String,
    val patientId: Long?,
    val patientName: String?,
    val appointmentId: Long?,
    val assignedToId: Long?,
    val assignedToName: String?,
    val dueAt: String?,
    val completionNote: String?,
    val completedById: Long?,
    val completedByName: String?,
    val completedAt: String?,
    val createdAt: String,
    val updatedAt: String
)

data class InstructionSummary(
    val pending: Long,
    val inProgress: Long,
    val done: Long,
    val cancelled: Long
)