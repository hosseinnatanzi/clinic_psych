package com.clinic.demo.domain.Entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "psychological_tests")
class PsychologicalTest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(nullable = false, length = 200) var title: String,
    @Column(columnDefinition = "TEXT") var description: String? = null,
    @Column(name = "is_active", nullable = false) var isActive: Boolean = true,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "created_by") var createdBy: User? = null,
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)