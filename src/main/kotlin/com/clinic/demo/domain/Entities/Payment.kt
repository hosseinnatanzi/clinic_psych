package com.clinic.demo.domain.Entities


import com.clinic.demo.domain.PaymentGateway
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import com.clinic.demo.domain.PaymentStatus

@Entity
@Table(name = "payments", indexes = [
    Index(name = "idx_pay_user", columnList = "user_id"),
    Index(name = "idx_pay_authority", columnList = "authority")])
class Payment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @Column(nullable = false, precision = 12, scale = 0) var amount: BigDecimal,
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    var gateway: PaymentGateway = PaymentGateway.ZARINPAL,
    @Column(unique = true, length = 120) var authority: String? = null,
    @Column(name = "ref_id", length = 120) var refId: String? = null,
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    var status: PaymentStatus = PaymentStatus.PENDING,
    @Column(length = 255) var description: String? = null,
    @Column(name = "related_type", length = 40) var relatedType: String? = null,
    @Column(name = "related_id") var relatedId: Long? = null,
    @Column(name = "paid_at") var paidAt: LocalDateTime? = null,
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)