package com.clinic.demo.controller


import com.clinic.demo.dto.*
import com.clinic.demo.security.currentUserId
import com.clinic.demo.service.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "پرداخت‌ها و زرین‌پال")
class PaymentController(private val svc: PaymentService) {

    @PostMapping("/initiate")
    @PreAuthorize("hasAnyAuthority('PATIENT','SECRETARY','ADMIN')")
    @Operation(summary = "شروع پرداخت — خروجی: redirectUrl زرین‌پال")
    fun initiate(@RequestBody req: InitiatePaymentRequest): InitiatePaymentResponse =
        svc.initiate(currentUserId(), req)

    @GetMapping("/callback")
    @Operation(summary = "Callback زرین‌پال (بدون احراز هویت)")
    fun callback(
        @RequestParam(name = "Authority", required = false) authority: String?,
        @RequestParam(name = "authority", required = false) auth2: String?
    ): String {
        val auth = authority ?: auth2 ?: return "no authority provided"
        val (ok, ref) = svc.verify(auth)
        return if (ok) "پرداخت موفق. شماره پیگیری: $ref"
        else "پرداخت ناموفق یا تایید نشد."
    }

    @GetMapping("/callback/json", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Callback به فرمت JSON")
    fun callbackJson(
        @RequestParam(name = "Authority", required = false) authority: String?,
        @RequestParam(name = "authority", required = false) auth2: String?
    ): CallbackResponse {
        val auth = authority ?: auth2
        ?: return CallbackResponse(false, null, "no authority provided")
        val (ok, ref) = svc.verify(auth)
        return if (ok) CallbackResponse(true, ref, "پرداخت موفق")
        else CallbackResponse(false, null, "پرداخت ناموفق")
    }

    @GetMapping("/my")
    @Operation(summary = "پرداخت‌های من")
    fun my(): List<PaymentSummary> = svc.listByUser(currentUserId())

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "جزئیات پرداخت")
    fun detail(@PathVariable id: Long): PaymentSummary = svc.detail(id, currentUserId())

    @GetMapping("/admin/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "همه‌ی پرداخت‌ها (فقط مدیر)")
    fun adminList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): List<PaymentSummary> = svc.listAll(page, size)

    @PutMapping("/{id}/refund")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "علامت‌گذاری پرداخت به‌عنوان مسترد (فقط مدیر)")
    fun refund(@PathVariable id: Long): MessageResponse {
        svc.refund(id); return MessageResponse("پرداخت به‌عنوان مسترد ثبت شد")
    }
}