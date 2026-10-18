package com.clinic.demo.domain.Entities

import com.clinic.demo.domain.UserRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true, nullable = false, length = 15)
    var mobile: String,

    @Column(unique = true, length = 120)
    var email: String? = null,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @Column(name = "first_name", nullable = false, length = 60)
    var firstName: String,

    @Column(name = "last_name", nullable = false, length = 60)
    var lastName: String,

    @Column(name = "national_code", unique = true, length = 10)
    var nationalCode: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var role: UserRole,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    val fullName: String get() = "$firstName $lastName"
}