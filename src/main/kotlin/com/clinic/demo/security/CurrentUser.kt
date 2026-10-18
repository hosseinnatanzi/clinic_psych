package com.clinic.demo.security

import org.springframework.security.core.context.SecurityContextHolder

fun currentUserId(): Long =
    (SecurityContextHolder.getContext().authentication?.principal as? Long)
        ?: throw IllegalStateException("کاربر احراز هویت نشده است")

/**
 * نقش کاربر جاری (PATIENT / DOCTOR / SECRETARY / ADMIN).
 */
fun currentRole(): String =
    SecurityContextHolder.getContext().authentication
        ?.authorities
        ?.firstOrNull()
        ?.authority
        ?: throw IllegalStateException("نقش کاربر یافت نشد")