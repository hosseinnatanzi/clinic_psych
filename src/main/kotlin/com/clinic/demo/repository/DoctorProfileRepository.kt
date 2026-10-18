package com.clinic.demo.repository

import com.clinic.demo.domain.Entities.DoctorProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DoctorProfileRepository : JpaRepository<DoctorProfile, Long> {
    fun findByUserId(userId: Long): DoctorProfile?
}