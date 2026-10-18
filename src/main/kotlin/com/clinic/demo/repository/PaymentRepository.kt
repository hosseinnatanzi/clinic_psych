package com.clinic.demo.repository

import com.clinic.demo.domain.Entities.Payment
import com.clinic.demo.domain.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface PaymentRepository : JpaRepository<Payment, Long> {
    fun findByAuthority(authority: String): Payment?
    fun findAllByUserId(userId: Long): List<Payment>
    fun findAllByStatus(status: PaymentStatus): List<Payment>

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID'")
    fun sumPaidAmount(): BigDecimal
}