package com.clinic.demo.dto


data class FinancialReport(
    val totalPaid: String, val countPaid: Long,
    val countPending: Long, val countFailed: Long
)

data class DashboardSummary(
    val totalPatients: Long, val totalDoctors: Long,
    val totalAppointments: Long, val totalClasses: Long,
    val totalPayments: Long, val totalRevenue: String,
    val appointmentsByStatus: Map<String, Long>
)

data class DoctorPerformance(val doctorId: Long, val doctorName: String, val appointmentCount: Long)
data class VisitRecord(
    val appointmentId: Long, val patientId: Long, val patientName: String,
    val doctorId: Long, val doctorName: String,
    val startAt: String, val status: String
)