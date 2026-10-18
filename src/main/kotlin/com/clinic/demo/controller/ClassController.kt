package com.clinic.demo.controller


import com.clinic.demo.dto.*
import com.clinic.demo.security.currentUserId
import com.clinic.demo.service.ClassService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/classes")
@Tag(name = "Classes", description = "مدیریت کلاس‌ها و ثبت‌نام")
class ClassController(private val svc: ClassService) {

    // ───────────── CRUD ─────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SECRETARY','ADMIN')")
    @Operation(summary = "ایجاد کلاس (منشی/مدیر)")
    fun create(@RequestBody req: CreateClassRequest): ClassResponse =
        svc.create(req, currentUserId())

    @GetMapping
    @Operation(summary = "لیست کلاس‌های فعال")
    fun list(): List<ClassResponse> = svc.listActive()

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('SECRETARY','ADMIN')")
    @Operation(summary = "همه‌ی کلاس‌ها (شامل غیرفعال)")
    fun listAll(): List<ClassResponse> = svc.listAll()

    @GetMapping("/{id}")
    @Operation(summary = "جزئیات کلاس")
    fun detail(@PathVariable id: Long): ClassResponse = svc.detail(id)

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SECRETARY','ADMIN')")
    @Operation(summary = "ویرایش کلاس")
    fun update(@PathVariable id: Long, @RequestBody req: UpdateClassRequest): ClassResponse =
        svc.update(id, req)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SECRETARY','ADMIN')")
    @Operation(summary = "غیرفعال کردن کلاس")
    fun delete(@PathVariable id: Long): MessageResponse {
        svc.delete(id); return MessageResponse("کلاس غیرفعال شد")
    }

    // ───────────── ثبت‌نام بیمار ─────────────
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('PATIENT','SECRETARY','ADMIN')")
    @Operation(summary = "ثبت‌نام در کلاس")
    fun register(@RequestBody req: RegisterClassRequest): MessageResponse {
        svc.register(req.classId, currentUserId())
        return MessageResponse("ثبت‌نام انجام شد")
    }

    @GetMapping("/my")
    @Operation(summary = "کلاس‌های من")
    fun my(): List<ClassRegistrationResponse> = svc.myRegistrations(currentUserId())

    @DeleteMapping("/{classId}/cancel")
    @Operation(summary = "لغو ثبت‌نام")
    fun cancel(@PathVariable classId: Long): MessageResponse {
        svc.cancel(classId, currentUserId())
        return MessageResponse("ثبت‌نام لغو شد")
    }

    // ───────────── مشاهده‌ی ثبت‌نام‌ها (منشی/مدیر) ─────────────
    @GetMapping("/{classId}/registrations")
    @PreAuthorize("hasAnyAuthority('SECRETARY','ADMIN','DOCTOR')")
    @Operation(summary = "لیست ثبت‌نام‌شدگان یک کلاس")
    fun registrations(@PathVariable classId: Long): List<ClassRegistrationResponse> =
        svc.registrationsForClass(classId)
}