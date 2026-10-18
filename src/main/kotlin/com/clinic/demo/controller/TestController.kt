package com.clinic.demo.controller


import com.clinic.demo.dto.*
import com.clinic.demo.security.currentUserId
import com.clinic.demo.service.TestService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tests")
@Tag(name = "Tests", description = "تست‌های روانشناسی و نتایج")
class TestController(private val svc: TestService) {

    // ───────────── مدیریت تست (دکتر/مدیر) ─────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR')")
    @Operation(summary = "ایجاد تست جدید")
    fun create(@RequestBody req: CreateTestRequest): Map<String, Long> =
        mapOf("id" to svc.createTest(req, currentUserId()))

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR')")
    @Operation(summary = "ویرایش تست")
    fun update(@PathVariable id: Long, @RequestBody req: UpdateTestRequest): MessageResponse {
        svc.updateTest(id, req); return MessageResponse("تست به‌روزرسانی شد")
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR')")
    @Operation(summary = "غیرفعال کردن تست")
    fun delete(@PathVariable id: Long): MessageResponse {
        svc.deactivateTest(id); return MessageResponse("تست غیرفعال شد")
    }

    // ───────────── مشاهده ─────────────
    @GetMapping
    @Operation(summary = "لیست تست‌های فعال")
    fun list(): List<TestResponse> = svc.listTests()

    @GetMapping("/{id}")
    @Operation(summary = "تست به همراه سوالات و گزینه‌ها")
    fun get(@PathVariable id: Long): TestWithQuestionsResponse = svc.getTestWithQuestions(id)

    // ───────────── مدیریت سوال/گزینه ─────────────
    @PostMapping("/{id}/questions")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR')")
    @Operation(summary = "افزودن سوال به تست")
    fun addQuestion(@PathVariable id: Long, @RequestBody req: AddQuestionRequest): Map<String, Long> =
        mapOf("id" to svc.addQuestion(id, req))

    @PostMapping("/questions/{id}/options")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR')")
    @Operation(summary = "افزودن گزینه به سوال")
    fun addOption(@PathVariable id: Long, @RequestBody req: AddOptionRequest): Map<String, Long> =
        mapOf("id" to svc.addOption(id, req))

    // ───────────── شرکت در تست (بیمار) ─────────────
    @PostMapping("/{id}/submit")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('PATIENT','DOCTOR','ADMIN')")
    @Operation(summary = "ثبت نتیجه‌ی تست")
    fun submit(@PathVariable id: Long, @RequestBody req: SubmitResultRequest): Map<String, Long> =
        mapOf("id" to svc.submitResult(id, currentUserId(), req))

    // ───────────── مشاهده‌ی نتایج ─────────────
    @GetMapping("/my/results")
    @Operation(summary = "نتایج تست‌های من")
    fun myResults(): List<TestResultResponse> = svc.myResults(currentUserId())

    @GetMapping("/patient/{patientId}/results")
    @PreAuthorize("hasAnyAuthority('DOCTOR','SECRETARY','ADMIN')")
    @Operation(summary = "نتایج تست‌های یک بیمار (دکتر/منشی/مدیر)")
    fun patientResults(@PathVariable patientId: Long): List<TestResultResponse> =
        svc.resultsForPatient(patientId)

    @GetMapping("/results/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "همه‌ی نتایج (فقط مدیر)")
    fun allResults(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): List<TestResultResponse> = svc.allResults(page, size)
}