package com.clinic.demo.service

import com.clinic.demo.domain.*
import com.clinic.demo.dto.DashboardSummary
import com.clinic.demo.dto.DoctorPerformance
import com.clinic.demo.dto.FinancialReport
import com.clinic.demo.dto.VisitRecord
import com.clinic.demo.repository.*
import org.springframework.stereotype.Service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.LocalDate

@Service
class ReportService(
    private val paymentRepository: PaymentRepository,
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository,
    private val classRepository: ClassRepository
) {
    // ─── financial (با فیلتر تاریخ اختیاری) ───
    fun financial(from: LocalDate?, to: LocalDate?): FinancialReport {
        val allPaid = paymentRepository.findAllByStatus(PaymentStatus.PAID)
        val filteredPaid = allPaid.filter { p ->
            val d = (p.paidAt ?: p.createdAt).toLocalDate()
            (from == null || !d.isBefore(from)) &&
                    (to == null || !d.isAfter(to))
        }
        val totalPaid = filteredPaid.fold(BigDecimal.ZERO) { a, b -> a + b.amount }

        val allPending = paymentRepository.findAllByStatus(PaymentStatus.PENDING)
        val allFailed  = paymentRepository.findAllByStatus(PaymentStatus.FAILED)

        return FinancialReport(
            totalPaid = totalPaid.toPlainString(),
            countPaid = filteredPaid.size.toLong(),
            countPending = allPending.size.toLong(),
            countFailed = allFailed.size.toLong()
        )
    }

    // ─── appointments by status ───
    fun appointmentsByStatus(): Map<String, Long> =
        appointmentRepository.countByStatus().associate { row ->
            val status = (row[0] as? Enum<*>)?.name ?: row[0].toString()
            val count = (row[1] as Number).toLong()
            status to count
        }

    // ─── counts ───
    fun patientsCount(): Long =
        userRepository.findAllByRole(UserRole.PATIENT).size.toLong()

    fun doctorsCount(): Long =
        userRepository.findAllByRole(UserRole.DOCTOR).size.toLong()

    // ─── top doctors (with names) ───
    fun topDoctors(limit: Int = 5): List<DoctorPerformance> =
        appointmentRepository.countByDoctor().take(limit).map { row ->
            val doctorId = (row[0] as Number).toLong()
            val count = (row[1] as Number).toLong()
            val name = userRepository.findById(doctorId)
                .map { it.fullName }.orElse("Unknown")
            DoctorPerformance(doctorId, name, count)
        }

    // ─── visit log (paginated) ───
    fun visitLog(page: Int, size: Int): List<VisitRecord> {
        val pageable = PageRequest.of(page, size, Sort.by("startAt").descending())
        return appointmentRepository.findAll(pageable).content.map { a ->
            VisitRecord(
                appointmentId = a.id!!,
                patientId = a.patient.id!!,
                patientName = a.patient.fullName,
                doctorId = a.doctor.id!!,
                doctorName = a.doctor.fullName,
                startAt = a.startAt.toString(),
                status = a.status.name
            )
        }
    }

    // ─── dashboard ───
    fun dashboard(): DashboardSummary = DashboardSummary(
        totalPatients = patientsCount(),
        totalDoctors = doctorsCount(),
        totalAppointments = appointmentRepository.count(),
        totalClasses = classRepository.count(),
        totalPayments = paymentRepository.count(),
        totalRevenue = paymentRepository.sumPaidAmount().toPlainString(),
        appointmentsByStatus = appointmentsByStatus()
    )
}