package com.clinic.demo.payment

import com.clinic.demo.exception.ApiException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

data class RequestPayload(val merchant_id: String, val amount: Long,
                          val description: String? = null, val callback_url: String)
data class VerifyPayload(val merchant_id: String, val amount: Long, val authority: String)
data class ZarinpalError(val code: Int, val message: String)
data class ZarinpalRequestData(val authority: String, val fee: Long, val code: Int)
data class ZarinpalVerifyData(val code: Int, val ref_id: Long, val card_pan: String? = null)
data class ZarinpalRequestResponse(val data: ZarinpalRequestData? = null, val errors: List<ZarinpalError>? = null)
data class ZarinpalVerifyResponse(val data: ZarinpalVerifyData? = null, val errors: List<ZarinpalError>? = null)

@Component
class ZarinpalClient(
    @Value("\${zarinpal.merchant-id}") private val merchantId: String,
    @Value("\${zarinpal.sandbox:true}") private val sandbox: Boolean
) {
    private val client: RestClient = RestClient.create()
    private val base = if (sandbox) "https://sandbox.zarinpal.com/pg"
    else "https://payment.zarinpal.com/pg"

    fun requestPayment(amountRials: Long, callbackUrl: String, description: String?): ZarinpalRequestData {
        val resp = client.post()
            .uri("$base/v4/payment/request.json")
            .contentType(MediaType.APPLICATION_JSON)
            .body(RequestPayload(merchantId, amountRials, description, callbackUrl))
            .retrieve()
            .body(ZarinpalRequestResponse::class.java)
        return resp?.data ?: throw ApiException(
            "Zarinpal request error: ${resp?.errors}", HttpStatus.BAD_GATEWAY)
    }

    fun verifyPayment(amountRials: Long, authority: String): ZarinpalVerifyData {
        val resp = client.post()
            .uri("$base/v4/payment/verify.json")
            .contentType(MediaType.APPLICATION_JSON)
            .body(VerifyPayload(merchantId, amountRials, authority))
            .retrieve()
            .body(ZarinpalVerifyResponse::class.java)
        return resp?.data ?: throw ApiException(
            "Zarinpal verify error: ${resp?.errors}", HttpStatus.BAD_GATEWAY)
    }

    fun redirectUrl(authority: String): String = "$base/StartPay/$authority"
}