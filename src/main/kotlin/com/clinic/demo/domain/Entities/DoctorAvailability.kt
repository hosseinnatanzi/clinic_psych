package com.clinic.demo.domain.Entities

import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "doctor_availability",
    uniqueConstraints = [UniqueConstraint(
        name = "uk_doc_dow_start",
        columnNames = ["doctor_id", "day_of_week", "start_time"])])
class DoctorAvailability(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "doctor_id", nullable = false)
    var doctor: User,
    @Column(name = "day_of_week", nullable = false) var dayOfWeek: Int,
    @Column(name = "start_time", nullable = false) var startTime: LocalTime,
    @Column(name = "end_time", nullable = false) var endTime: LocalTime,
    @Column(name = "is_active", nullable = false) var isActive: Boolean = true
)