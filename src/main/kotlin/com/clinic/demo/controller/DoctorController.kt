package com.clinic.demo.controller


import com.clinic.demo.dto.*
import com.clinic.demo.security.currentUserId
import com.clinic.demo.service.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/doctors")
@Tag(name = "Doctors", description = "مدیریت دکترها و برنامه‌ی حضور")
class DoctorController(private val doctorService: DoctorService) {

    // ───────────── پروفایل ─────────────
    @GetMapping
    @Operation(summary = "لیست همه‌ی دکترها")
    fun list(): List<DoctorSummary> = doctorService.listDoctors()

    @GetMapping("/{id}")
    @Operation(summary = "جزئیات یک دکتر")
    fun detail(@PathVariable id: Long): Map<String, Any?> = doctorService.detail(id)

    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('DOCTOR')")
    @Operation(summary = "ویرایش پروفایل دکتر خودم")
    fun updateProfile(@RequestBody req: UpdateDoctorProfileRequest): MessageResponse {
        doctorService.updateProfile(currentUserId(), req)
        return MessageResponse("پروفایل به‌روزرسانی شد")
    }

    // ───────────── برنامه‌ی هفتگی ─────────────
    @GetMapping("/availability")
    @PreAuthorize("hasAuthority('DOCTOR')")
    @Operation(summary = "برنامه‌ی هفتگی من")
    fun myWeekly(): List<Map<String, Any?>> = doctorService.weekly(currentUserId())

    @GetMapping("/{id}/availability")
    @Operation(summary = "برنامه‌ی هفتگی یک دکتر (برای بیمار)")
    fun weekly(@PathVariable id: Long): List<Map<String, Any?>> = doctorService.weekly(id)

    @PostMapping("/availability")
    @PreAuthorize("hasAuthority('DOCTOR')")
    @Operation(summary = "افزودن بازه‌ی هفتگی")
    fun addWeekly(@RequestBody req: WeeklyAvailabilityRequest): MessageResponse {
        doctorService.addWeekly(currentUserId(), req)
        return MessageResponse("بازه‌ی هفتگی اضافه شد")
    }

    @DeleteMapping("/availability/{id}")
    @PreAuthorize("hasAuthority('DOCTOR')")
    @Operation(summary = "حذف بازه‌ی هفتگی")
    fun deleteWeekly(@PathVariable id: Long): MessageResponse {
        doctorService.deleteWeekly(id, currentUserId())
        return MessageResponse("بازه‌ی هفتگی حذف شد")
    }

    // ───────────── روزهای خاص ─────────────
    @GetMapping("/{id}/special-days")
    @Operation(summary = "روزهای خاص یک دکتر")
    fun specialDays(@PathVariable id: Long): List<Map<String, Any?>> =
        doctorService.specialDays(id)

    @PostMapping("/special-days")
    @PreAuthorize("hasAuthority('DOCTOR')")
    @Operation(summary = "افزودن روز خاص (تعطیلی/اضافه‌کاری)")
    fun addSpecialDay(@RequestBody req: SpecialDayRequest): MessageResponse {
        doctorService.addSpecialDay(currentUserId(), req)
        return MessageResponse("روز خاص اضافه شد")
    }

    @DeleteMapping("/special-days/{id}")
    @PreAuthorize("hasAuthority('DOCTOR')")
    @Operation(summary = "حذف روز خاص")
    fun deleteSpecialDay(@PathVariable id: Long): MessageResponse {
        doctorService.deleteSpecialDay(id, currentUserId())
        return MessageResponse("روز خاص حذف شد")
    }

    // ───────────── اسلات‌های آزاد ─────────────
    @GetMapping("/{id}/slots")
    @Operation(summary = "لیست اسلات‌های آزاد دکتر")
    fun slots(
        @PathVariable id: Long,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
        @RequestParam(defaultValue = "7") days: Int
    ): List<String> = doctorService.slots(id, from ?: LocalDate.now(), days)
}