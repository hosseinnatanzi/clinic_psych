package com.clinic.demo.domain.Entities


import com.clinic.demo.domain.AppointmentStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "appointments", indexes = [
    Index(name = "idx_appt_doctor_date", columnList = "doctor_id,start_at"),
    Index(name = "idx_appt_patient", columnList = "patient_id")])
class Appointment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "patient_id", nullable = false)
    var patient: User,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "doctor_id", nullable = false)
    var doctor: User,
    @Column(name = "start_at", nullable = false) var startAt: LocalDateTime,
    @Column(name = "end_at", nullable = false) var endAt: LocalDateTime,
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    var status: AppointmentStatus = AppointmentStatus.PENDING,
    @Column(columnDefinition = "TEXT") var notes: String? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "created_by")
    var createdBy: User? = null,
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)