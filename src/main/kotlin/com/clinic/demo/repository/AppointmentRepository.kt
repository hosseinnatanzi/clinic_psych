package com.clinic.demo.repository

import com.clinic.demo.domain.Entities.Appointment
import com.clinic.demo.domain.AppointmentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AppointmentRepository : JpaRepository<Appointment, Long> {

    fun findAllByPatientId(patientId: Long): List<Appointment>
    fun findAllByDoctorId(doctorId: Long): List<Appointment>

    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a
        WHERE a.doctor.id = :doctorId
          AND a.status <> 'CANCELLED'
          AND a.startAt < :endAt
          AND a.endAt > :startAt
    """)
    fun existsOverlap(
        @Param("doctorId") doctorId: Long,
        @Param("startAt") startAt: LocalDateTime,
        @Param("endAt") endAt: LocalDateTime
    ): Boolean

    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    fun countByStatus(): List<Array<Any>>

    @Query("""
        SELECT a.doctor.id, COUNT(a) FROM Appointment a
        GROUP BY a.doctor.id ORDER BY COUNT(a) DESC
    """)
    fun countByDoctor(): List<Array<Any>>
    @Query("""
    SELECT COUNT(a) > 0 FROM Appointment a
    WHERE a.doctor.id = :doctorId
      AND a.id <> :excludeId
      AND a.status <> 'CANCELLED'
      AND a.startAt < :endAt
      AND a.endAt > :startAt
""")
    fun existsOverlapExcludingId(
        @Param("doctorId") doctorId: Long,
        @Param("excludeId") excludeId: Long,
        @Param("startAt") startAt: LocalDateTime,
        @Param("endAt") endAt: LocalDateTime
    ): Boolean
}