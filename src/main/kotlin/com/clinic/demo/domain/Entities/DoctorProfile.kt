package com.clinic.demo.domain.Entities

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "doctor_profiles")
class DoctorProfile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User,

    @Column(length = 120)
    var specialty: String? = null,

    @Column(columnDefinition = "TEXT")
    var bio: String? = null,

    @Column(name = "license_number", length = 60)
    var licenseNumber: String? = null,

    @Column(name = "consultation_fee", nullable = false, precision = 12, scale = 0)
    var consultationFee: BigDecimal = BigDecimal.ZERO
)