package com.clinic.demo.repository

import com.clinic.demo.domain.Entities.DoctorAvailability
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DoctorAvailabilityRepository : JpaRepository<DoctorAvailability, Long> {
    fun findAllByDoctorIdAndIsActiveTrue(doctorId: Long): List<DoctorAvailability>
    fun findAllByDoctorId(doctorId: Long): List<DoctorAvailability>
}