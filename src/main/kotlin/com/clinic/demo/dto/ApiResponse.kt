package com.clinic.demo.dto


data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun <T> ok(data: T, message: String? = null) =
            ApiResponse(true, message, data)

        fun <T> okMessage(message: String) =
            ApiResponse<T>(true, message, null)

        fun <T> error(message: String) =
            ApiResponse<T>(false, message, null)
    }
}