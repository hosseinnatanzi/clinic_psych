package com.clinic.demo.domain.Entities


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "activity_logs")
class ActivityLog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") var user: User? = null,
    @Column(nullable = false, length = 100) var action: String,
    @Column(name = "entity_type", length = 50) var entityType: String? = null,
    @Column(name = "entity_id") var entityId: Long? = null,
    @Column(columnDefinition = "TEXT") var details: String? = null,
    @Column(name = "ip_address", length = 45) var ipAddress: String? = null,
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)