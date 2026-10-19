package com.clinic.demo.controller

import com.clinic.demo.dto.*
import com.clinic.demo.security.currentRole
import com.clinic.demo.security.currentUserId
import com.clinic.demo.service.InstructionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/instructions")
@Tag(name = "Instructions", description = "دستور کار دکتر به منشی برای پیگیری")
class InstructionController(private val svc: InstructionService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('DOCTOR')")
    @Operation(summary = "ثبت دستور کار جدید (فقط دکتر)")
    fun create(@Valid @RequestBody req: CreateInstructionRequest): InstructionResponse =
        svc.create(currentUserId(), req)

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('DOCTOR')")
    @Operation(summary = "دستورات ثبت‌شده توسط من (دکتر)")
    fun my(): List<InstructionResponse> = svc.myInstructions(currentUserId())

    @GetMapping("/assigned")
    @PreAuthorize("hasAuthority('SECRETARY')")
    @Operation(summary = "دستورات اختصاص‌یافته به من (منشی)")
    fun assigned(): List<InstructionResponse> = svc.assignedToMe(currentUserId())

    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('SECRETARY','ADMIN')")
    @Operation(summary = "لیست دستورات باز (منتظر / در حال انجام)")
    fun pending(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): List<InstructionResponse> = svc.pendingList(page, size)

    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('SECRETARY','ADMIN','DOCTOR')")
    @Operation(summary = "خلاصه تعداد دستورات بر اساس وضعیت")
    fun summary(): InstructionSummary = svc.summary()

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('DOCTOR','SECRETARY','ADMIN')")
    @Operation(summary = "جزئیات یک دستور کار")
    fun detail(@PathVariable id: Long): InstructionResponse = svc.detail(id)

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyAuthority('DOCTOR','SECRETARY','ADMIN')")
    @Operation(summary = "دستورات مرتبط با یک بیمار")
    fun byPatient(@PathVariable patientId: Long): List<InstructionResponse> =
        svc.byPatient(patientId)

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('DOCTOR','SECRETARY','ADMIN')")
    @Operation(summary = "به‌روزرسانی وضعیت / اختصاص / یادداشت انجام")
    fun update(
        @PathVariable id: Long,
        @RequestBody req: UpdateInstructionRequest
    ): InstructionResponse = svc.update(id, currentUserId(), currentRole(), req)
}