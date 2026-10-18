package com.clinic.demo.repository

import com.clinic.demo.domain.Entities.ClassRegistration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClassRegistrationRepository : JpaRepository<ClassRegistration, Long> {
    fun findAllByPatientId(patientId: Long): List<ClassRegistration>
    fun countByTrainingClassId(classId: Long): Long
    fun findAllByTrainingClassId(classId: Long): List<ClassRegistration>
    fun findByTrainingClassIdAndPatientId(classId: Long, patientId: Long): ClassRegistration?
}