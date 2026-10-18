package com.clinic.demo.domain.Entities


import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "classes")
class ClassEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(nullable = false, length = 200) var title: String,
    @Column(columnDefinition = "TEXT") var description: String? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "instructor_id") var instructor: User? = null,
    @Column(name = "start_at", nullable = false) var startAt: LocalDateTime,
    @Column(name = "end_at", nullable = false) var endAt: LocalDateTime,
    @Column(nullable = false) var capacity: Int = 10,
    @Column(nullable = false, precision = 12, scale = 0) var price: BigDecimal = BigDecimal.ZERO,
    @Column(name = "is_active", nullable = false) var isActive: Boolean = true,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "created_by") var createdBy: User? = null,
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)