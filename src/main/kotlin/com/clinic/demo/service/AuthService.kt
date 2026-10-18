package com.clinic.demo.service
import com.clinic.demo.exception.ApiException

import com.clinic.demo.domain.Entities.User
import com.clinic.demo.domain.UserRole
import com.clinic.demo.dto.*
import com.clinic.demo.repository.*
import org.mindrot.jbcrypt.BCrypt
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {
    @Transactional
    fun register(req: RegisterRequest): AuthResponse {
        if (req.password.length < 6)
            throw ApiException("رمز عبور حداقل ۶ کاراکتر", HttpStatus.BAD_REQUEST)
        if (userRepository.existsByMobile(req.mobile))
            throw ApiException("این شماره قبلاً ثبت شده", HttpStatus.CONFLICT)

        val role = try { UserRole.valueOf(req.role) } catch (e: Exception) { UserRole.PATIENT }
        val user = userRepository.save(
            User(
                mobile = req.mobile, email = req.email,
                passwordHash = BCrypt.hashpw(req.password, BCrypt.gensalt(12)),
                firstName = req.firstName, lastName = req.lastName,
                nationalCode = req.nationalCode, role = role
            )
        )
        return AuthResponse(jwtService.generateToken(user.id!!, user.role.name), user.id!!, user.role.name)
    }

    fun login(req: LoginRequest): AuthResponse {
        val user = userRepository.findByMobile(req.mobile)
            ?: throw ApiException("کاربر یافت نشد", HttpStatus.UNAUTHORIZED)
        if (!user.isActive)
            throw ApiException("حساب غیرفعال است", HttpStatus.FORBIDDEN)
        if (!BCrypt.checkpw(req.password, user.passwordHash))
            throw ApiException("رمز عبور اشتباه", HttpStatus.UNAUTHORIZED)
        return AuthResponse(jwtService.generateToken(user.id!!, user.role.name), user.id!!, user.role.name)
    }

    // ─── helpers برای AuthController.me() ───
    fun extractUserId(token: String): Long? = jwtService.extractUserId(token)
    fun extractRole(token: String): String? = jwtService.extractRole(token)
}