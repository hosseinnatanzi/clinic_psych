package com.clinic.demo.domain.Entities

import com.clinic.demo.domain.InstructionPriority
import com.clinic.demo.domain.InstructionStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "doctor_instructions",
    indexes = [
        Index(name = "idx_instr_status", columnList = "status"),
        Index(name = "idx_instr_doctor", columnList = "doctor_id"),
        Index(name = "idx_instr_patient", columnList = "patient_id"),
        Index(name = "idx_instr_assigned", columnList = "assigned_to")
    ]
)
class DoctorInstruction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    var doctor: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    var assignedTo: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    var patient: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    var appointment: Appointment? = null,

    @Column(nullable = false, length = 200)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: InstructionStatus = InstructionStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var priority: InstructionPriority = InstructionPriority.NORMAL,

    @Column(name = "due_at")
    var dueAt: LocalDateTime? = null,

    @Column(name = "completion_note", columnDefinition = "TEXT")
    var completionNote: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by")
    var completedBy: User? = null,

    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)