package com.clinic.demo.dto


data class BookAppointmentRequest(
    val doctorId: Long, val startAt: String,
    val durationMin: Long = 30, val notes: String? = null
)

data class RescheduleRequest(
    val newStartAt: String, val durationMin: Long = 30
)

data class ChangeStatusRequest(val status: String)

data class AppointmentResponse(
    val id: Long, val patientId: Long, val doctorId: Long,
    val patientName: String? = null, val doctorName: String? = null,
    val startAt: String, val endAt: String, val status: String,
    val notes: String? = null
)

data class WeeklyAvailabilityRequest(
    val dayOfWeek: Int, val startTime: String, val endTime: String
)

data class SpecialDayRequest(
    val date: String, val available: Boolean,
    val startTime: String? = null, val endTime: String? = null, val note: String? = null
)