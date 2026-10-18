package com.clinic.demo.dto


data class InitiatePaymentRequest(
    val amount: String, val description: String? = null,
    val relatedType: String? = null, val relatedId: Long? = null
)

data class InitiatePaymentResponse(val paymentId: Long, val redirectUrl: String)

data class PaymentSummary(
    val id: Long, val amount: String, val status: String,
    val refId: String? = null, val gateway: String,
    val description: String? = null, val createdAt: String? = null,
    val userId: Long? = null, val userName: String? = null
)

data class CallbackResponse(val ok: Boolean, val refId: String?, val message: String)