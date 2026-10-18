package com.clinic.demo.controller


import com.clinic.demo.dto.*
import com.clinic.demo.service.ReportService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "گزارش‌های مالی و آماری (مدیر/منشی)")
class ReportController(private val svc: ReportService) {

    @GetMapping("/financial")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "گزارش مالی (فقط مدیر)")
    fun financial(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?
    ): FinancialReport = svc.financial(from, to)

    @GetMapping("/appointments")
    @PreAuthorize("hasAnyAuthority('ADMIN','SECRETARY','DOCTOR')")
    @Operation(summary = "آمار نوبت‌ها به تفکیک وضعیت")
    fun appointments(): Map<String, Long> = svc.appointmentsByStatus()

    @GetMapping("/patients/count")
    @PreAuthorize("hasAnyAuthority('ADMIN','SECRETARY')")
    @Operation(summary = "تعداد بیماران")
    fun patientsCount(): Map<String, Long> = mapOf("count" to svc.patientsCount())

    @GetMapping("/doctors/count")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "تعداد دکترها")
    fun doctorsCount(): Map<String, Long> = mapOf("count" to svc.doctorsCount())

    @GetMapping("/doctors/top")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "پرتعدادترین دکترها")
    fun topDoctors(@RequestParam(defaultValue = "5") limit: Int): List<DoctorPerformance> =
        svc.topDoctors(limit)

    @GetMapping("/visits")
    @PreAuthorize("hasAnyAuthority('ADMIN','SECRETARY')")
    @Operation(summary = "گزارش مراجعه‌کنندگان")
    fun visits(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): List<VisitRecord> = svc.visitLog(page, size)

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "خلاصه‌ی داشبورد مدیر")
    fun dashboard(): DashboardSummary = svc.dashboard()
}