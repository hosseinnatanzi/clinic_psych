package com.clinic.demo.repository

import com.clinic.demo.domain.Entities.DoctorInstruction
import com.clinic.demo.domain.InstructionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DoctorInstructionRepository : JpaRepository<DoctorInstruction, Long> {

    fun findAllByDoctorIdOrderByCreatedAtDesc(doctorId: Long): List<DoctorInstruction>

    fun findAllByAssignedToIdOrderByCreatedAtDesc(secretaryId: Long): List<DoctorInstruction>

    fun findAllByPatientIdOrderByCreatedAtDesc(patientId: Long): List<DoctorInstruction>

    fun findAllByStatusOrderByPriorityDescCreatedAtDesc(status: InstructionStatus): List<DoctorInstruction>

    @Query("""
        SELECT i FROM DoctorInstruction i
        WHERE i.status IN :statuses
        ORDER BY 
            CASE i.priority 
                WHEN com.clinic.demo.domain.InstructionPriority.HIGH THEN 0
                WHEN com.clinic.demo.domain.InstructionPriority.NORMAL THEN 1
                ELSE 2 
            END,
            i.createdAt DESC
    """)
    fun findPendingSorted(
        @Param("statuses") statuses: List<InstructionStatus>,
        pageable: Pageable
    ): Page<DoctorInstruction>

    fun countByStatus(status: InstructionStatus): Long
}