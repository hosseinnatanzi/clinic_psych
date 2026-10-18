package com.clinic.demo.repository
import com.clinic.demo.domain.Entities.DoctorSpecialDay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DoctorSpecialDayRepository : JpaRepository<DoctorSpecialDay, Long> {
    fun findAllByDoctorId(doctorId: Long): List<DoctorSpecialDay>
}