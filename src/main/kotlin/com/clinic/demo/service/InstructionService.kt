package com.clinic.demo.service

import com.clinic.demo.domain.*
import com.clinic.demo.domain.Entities.*
import com.clinic.demo.dto.*
import com.clinic.demo.exception.ApiException
import com.clinic.demo.repository.*
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class InstructionService(
    private val instructionRepository: DoctorInstructionRepository,
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val activityLogService: ActivityLogService
) {
    private fun DoctorInstruction.toResponse() = InstructionResponse(
        id = id!!,
        title = title,
        content = content,
        status = status.name,
        priority = priority.name,
        doctorId = doctor.id!!,
        doctorName = doctor.fullName,
        patientId = patient?.id,
        patientName = patient?.fullName,
        appointmentId = appointment?.id,
        assignedToId = assignedTo?.id,
        assignedToName = assignedTo?.fullName,
        dueAt = dueAt?.toString(),
        completionNote = completionNote,
        completedById = completedBy?.id,
        completedByName = completedBy?.fullName,
        completedAt = completedAt?.toString(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )

    private fun findOrThrow(id: Long): DoctorInstruction =
        instructionRepository.findById(id).orElseThrow {
            ApiException("دستور کار یافت نشد", HttpStatus.NOT_FOUND)
        }

    private fun findUser(id: Long, expectedRole: UserRole? = null): User {
        val u = userRepository.findById(id).orElseThrow {
            ApiException("کاربر یافت نشد", HttpStatus.NOT_FOUND)
        }
        if (expectedRole != null && u.role != expectedRole)
            throw ApiException("نقش کاربر مطابق انتظار نیست ($expectedRole)", HttpStatus.BAD_REQUEST)
        return u
    }

    @Transactional
    fun create(doctorId: Long, req: CreateInstructionRequest): InstructionResponse {
        val doctor = findUser(doctorId, UserRole.DOCTOR)
        val patient = req.patientId?.let { findUser(it, UserRole.PATIENT) }
        val appointment = req.appointmentId?.let {
            appointmentRepository.findById(it).orElseThrow {
                ApiException("نوبت یافت نشد", HttpStatus.NOT_FOUND)
            }
        }
        val assignee = req.assignedToId?.let { findUser(it, UserRole.SECRETARY) }

        val priority = try {
            InstructionPriority.valueOf(req.priority.uppercase())
        } catch (e: Exception) {
            InstructionPriority.NORMAL
        }

        val dueAt = req.dueAt?.let {
            try { LocalDateTime.parse(it) }
            catch (e: Exception) {
                throw ApiException("dueAt نامعتبر (فرمت: 2026-10-21T14:00:00)", HttpStatus.BAD_REQUEST)
            }
        }

        val instruction = instructionRepository.save(
            DoctorInstruction(
                doctor = doctor,
                assignedTo = assignee,
                patient = patient,
                appointment = appointment,
                title = req.title.trim(),
                content = req.content.trim(),
                priority = priority,
                dueAt = dueAt
            )
        )

        activityLogService.log(
            userId = doctorId,
            action = "CREATE_INSTRUCTION",
            entityType = "DoctorInstruction",
            entityId = instruction.id,
            details = "دستور کار: ${instruction.title}"
        )
        return instruction.toResponse()
    }

    fun myInstructions(doctorId: Long): List<InstructionResponse> =
        instructionRepository.findAllByDoctorIdOrderByCreatedAtDesc(doctorId).map { it.toResponse() }

    fun assignedToMe(secretaryId: Long): List<InstructionResponse> =
        instructionRepository.findAllByAssignedToIdOrderByCreatedAtDesc(secretaryId).map { it.toResponse() }

    fun byPatient(patientId: Long): List<InstructionResponse> =
        instructionRepository.findAllByPatientIdOrderByCreatedAtDesc(patientId).map { it.toResponse() }

    fun pendingList(page: Int, size: Int): List<InstructionResponse> {
        val pageable = PageRequest.of(page.coerceAtLeast(0), size.coerceIn(1, 50))
        val statuses = listOf(InstructionStatus.PENDING, InstructionStatus.IN_PROGRESS)
        return instructionRepository.findPendingSorted(statuses, pageable).content.map { it.toResponse() }
    }

    fun detail(id: Long): InstructionResponse = findOrThrow(id).toResponse()

    @Transactional
    fun update(id: Long, actorId: Long, actorRole: String, req: UpdateInstructionRequest): InstructionResponse {
        val instruction = findOrThrow(id)

        val isOwnerDoctor = instruction.doctor.id == actorId
        val isAssignedSecretary = instruction.assignedTo?.id == actorId
        val isAdmin = actorRole == "ADMIN"
        val isSecretary = actorRole == "SECRETARY"

        if (!isOwnerDoctor && !isAssignedSecretary && !isAdmin && !isSecretary)
            throw ApiException("دسترسی ندارید", HttpStatus.FORBIDDEN)

        req.status?.let { statusStr ->
            val newStatus = try {
                InstructionStatus.valueOf(statusStr.uppercase())
            } catch (e: Exception) {
                throw ApiException("status نامعتبر", HttpStatus.BAD_REQUEST)
            }
            instruction.status = newStatus
            if (newStatus == InstructionStatus.DONE || newStatus == InstructionStatus.CANCELLED) {
                instruction.completedAt = LocalDateTime.now()
                instruction.completedBy = userRepository.findById(actorId).orElse(null)
            }
        }

        req.assignedToId?.let { secId ->
            instruction.assignedTo = findUser(secId, UserRole.SECRETARY)
        }

        req.completionNote?.let { instruction.completionNote = it.trim() }

        req.priority?.let { p ->
            instruction.priority = try {
                InstructionPriority.valueOf(p.uppercase())
            } catch (e: Exception) {
                throw ApiException("priority نامعتبر", HttpStatus.BAD_REQUEST)
            }
        }

        instruction.updatedAt = LocalDateTime.now()
        instructionRepository.save(instruction)

        activityLogService.log(
            userId = actorId,
            action = "UPDATE_INSTRUCTION",
            entityType = "DoctorInstruction",
            entityId = id,
            details = "وضعیت: ${instruction.status}"
        )
        return instruction.toResponse()
    }

    fun summary(): InstructionSummary = InstructionSummary(
        pending = instructionRepository.countByStatus(InstructionStatus.PENDING),
        inProgress = instructionRepository.countByStatus(InstructionStatus.IN_PROGRESS),
        done = instructionRepository.countByStatus(InstructionStatus.DONE),
        cancelled = instructionRepository.countByStatus(InstructionStatus.CANCELLED)
    )
}