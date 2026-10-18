package com.clinic.demo.controller


import com.clinic.demo.dto.*
import com.clinic.demo.security.*
 import com.clinic.demo.service.AppointmentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "مدیریت نوبت‌ها")
class AppointmentController(private val svc: AppointmentService) {

    // ───────────── بیمار ─────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('PATIENT','SECRETARY','ADMIN')")
    @Operation(summary = "رزرو نوبت")
    fun book(@RequestBody req: BookAppointmentRequest): Map<String, Long> =
        mapOf("id" to svc.book(currentUserId(), req).id)

    @GetMapping("/my")
    @Operation(summary = "نوبت‌های من")
    fun my(): List<AppointmentResponse> = svc.myAppointments(currentUserId())

    // ───────────── تغییر وضعیت ─────────────
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('PATIENT','SECRETARY','DOCTOR','ADMIN')")
    @Operation(summary = "تغییر وضعیت نوبت")
    fun changeStatus(
        @PathVariable id: Long,
        @RequestBody req: ChangeStatusRequest
    ): MessageResponse {
        svc.changeStatus(id, req.status, currentRole(), currentUserId())
        return MessageResponse("وضعیت به‌روزرسانی شد")
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyAuthority('SECRETARY','ADMIN')")
    @Operation(summary = "جابجایی نوبت (منشی/مدیر)")
    fun reschedule(@PathVariable id: Long, @RequestBody req: RescheduleRequest): MessageResponse {
        svc.reschedule(id, req)
        return MessageResponse("نوبت جابجا شد")
    }

    // ───────────── مشاهده ─────────────
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "جزئیات یک نوبت")
    fun detail(@PathVariable id: Long): AppointmentResponse = svc.detail(id)

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyAuthority('DOCTOR','SECRETARY','ADMIN')")
    @Operation(summary = "نوبت‌های یک دکتر")
    fun doctorAppointments(@PathVariable doctorId: Long): List<AppointmentResponse> =
        svc.doctorAppointments(doctorId)

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SECRETARY','ADMIN')")
    @Operation(summary = "همه‌ی نوبت‌ها (منشی/مدیر)")
    fun all(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): List<AppointmentResponse> = svc.allAppointments(page, size)

    // ───────────── اسلات‌ها ─────────────
    @GetMapping("/doctor/{doctorId}/slots")
    @Operation(summary = "اسلات‌های آزاد یک دکتر")
    fun slots(
        @PathVariable doctorId: Long,
        @RequestParam(required = false) from: String?,
        @RequestParam(defaultValue = "7") days: Int
    ): List<String> = svc.doctorSlots(doctorId, from, days)
}