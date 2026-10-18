package com.clinic.demo.domain.Entities


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "test_results")
class TestResult(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "test_id", nullable = false)
    var test: PsychologicalTest,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "patient_id", nullable = false)
    var patient: User,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "doctor_id")
    var doctor: User? = null,
    @Column(name = "total_score") var totalScore: Int? = null,
    @Column(name = "result_summary", columnDefinition = "TEXT") var resultSummary: String? = null,
    @Column(columnDefinition = "TEXT") var answers: String? = null,
    @Column(name = "taken_at", nullable = false, updatable = false)
    var takenAt: LocalDateTime = LocalDateTime.now()
)