package com.clinic.demo.service

import com.clinic.demo.domain.*
import com.clinic.demo.domain.Entities.*
import com.clinic.demo.dto.*
import com.clinic.demo.exception.ApiException
import com.clinic.demo.payment.ZarinpalClient
import com.clinic.demo.repository.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort


@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val userRepository: UserRepository,
    private val zarinpal: ZarinpalClient,
    @Value("\${zarinpal.callback-base}") private val callbackBase: String
) {
    private fun Payment.toSummary() = PaymentSummary(
        id = id!!,
        amount = amount.toPlainString(),
        status = status.name,
        refId = refId,
        gateway = gateway.name,
        description = description,
        createdAt = createdAt.toString(),
        userId = user.id,
        userName = user.fullName
    )

    // ─── initiate ───
    @Transactional
    fun initiate(userId: Long, req: InitiatePaymentRequest): InitiatePaymentResponse {
        val user = userRepository.findById(userId).orElseThrow {
            ApiException("کاربر یافت نشد", HttpStatus.NOT_FOUND)
        }
        val amount = try { BigDecimal(req.amount) }
        catch (e: Exception) { throw ApiException("مبلغ نامعتبر", HttpStatus.BAD_REQUEST) }
        if (amount.signum() <= 0)
            throw ApiException("مبلغ باید مثبت باشد", HttpStatus.BAD_REQUEST)

        val payment = paymentRepository.save(
            Payment(
                user = user, amount = amount, gateway = PaymentGateway.ZARINPAL,
                description = req.description,
                relatedType = req.relatedType, relatedId = req.relatedId
            )
        )
        val callback = "$callbackBase/api/v1/payments/callback"
        val resp = zarinpal.requestPayment(amount.toLong() * 10, callback, req.description)
        payment.authority = resp.authority
        paymentRepository.save(payment)
        return InitiatePaymentResponse(payment.id!!, zarinpal.redirectUrl(resp.authority))
    }

    // ─── verify ───
    @Transactional
    fun verify(authority: String): Pair<Boolean, String> {
        val payment = paymentRepository.findByAuthority(authority)
            ?: return false to "not_found"
        return try {
            val data = zarinpal.verifyPayment(payment.amount.toLong() * 10, authority)
            payment.status = PaymentStatus.PAID
            payment.refId = data.ref_id.toString()
            payment.paidAt = LocalDateTime.now()
            paymentRepository.save(payment)
            true to data.ref_id.toString()
        } catch (e: Exception) {
            payment.status = PaymentStatus.FAILED
            paymentRepository.save(payment)
            false to (e.message ?: "verify_failed")
        }
    }

    // ─── list my ───
    fun listByUser(userId: Long): List<PaymentSummary> =
        paymentRepository.findAllByUserId(userId).map { it.toSummary() }

    // ─── detail (با بررسی مالکیت) ───
    fun detail(id: Long, userId: Long): PaymentSummary {
        val p = paymentRepository.findById(id).orElseThrow {
            ApiException("پرداخت یافت نشد", HttpStatus.NOT_FOUND)
        }
        if (p.user.id != userId)
            throw ApiException("دسترسی ندارید", HttpStatus.FORBIDDEN)
        return p.toSummary()
    }

    // ─── admin list (paginated) ───
    fun listAll(page: Int, size: Int): List<PaymentSummary> {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        return paymentRepository.findAll(pageable).content.map { it.toSummary() }
    }

    // ─── refund ───
    @Transactional
    fun refund(id: Long) {
        val p = paymentRepository.findById(id).orElseThrow {
            ApiException("پرداخت یافت نشد", HttpStatus.NOT_FOUND)
        }
        if (p.status != PaymentStatus.PAID)
            throw ApiException("فقط پرداخت‌های موفق قابل استرداد هستند", HttpStatus.BAD_REQUEST)
        p.status = PaymentStatus.REFUNDED
        paymentRepository.save(p)
    }
}