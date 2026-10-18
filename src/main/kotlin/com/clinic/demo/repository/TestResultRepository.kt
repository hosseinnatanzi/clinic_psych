package com.clinic.demo.repository

import com.clinic.demo.domain.Entities.TestResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestResultRepository : JpaRepository<TestResult, Long> {
    fun findAllByPatientId(patientId: Long): List<TestResult>
}