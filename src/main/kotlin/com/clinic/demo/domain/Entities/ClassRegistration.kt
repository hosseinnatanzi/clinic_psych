package com.clinic.demo.domain.Entities


import com.clinic.demo.domain.RegistrationStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "class_registrations",
    uniqueConstraints = [UniqueConstraint(
        name = "uk_class_patient", columnNames = ["class_id", "patient_id"])])
class ClassRegistration(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "class_id", nullable = false)
    var trainingClass: ClassEntity,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "patient_id", nullable = false)
    var patient: User,
    @Column(name = "registered_at", nullable = false, updatable = false)
    var registeredAt: LocalDateTime = LocalDateTime.now(),
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    var status: RegistrationStatus = RegistrationStatus.REGISTERED,
    @Column(name = "payment_id") var paymentId: Long? = null
)