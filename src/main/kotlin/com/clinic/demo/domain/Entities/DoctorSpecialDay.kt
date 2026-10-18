package com.clinic.demo.domain.Entities


import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "doctor_special_days",
    uniqueConstraints = [UniqueConstraint(
        name = "uk_doc_date", columnNames = ["doctor_id", "date"])])
class DoctorSpecialDay(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "doctor_id", nullable = false)
    var doctor: User,
    @Column(nullable = false) var date: LocalDate,
    @Column(name = "is_available", nullable = false) var isAvailable: Boolean = false,
    @Column(name = "start_time") var startTime: LocalTime? = null,
    @Column(name = "end_time") var endTime: LocalTime? = null,
    @Column(length = 255) var note: String? = null
)