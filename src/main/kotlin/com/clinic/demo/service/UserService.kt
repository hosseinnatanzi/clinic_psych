package com.clinic.demo.service


import com.clinic.demo.domain.Entities.User
import com.clinic.demo.domain.UserRole
import com.clinic.demo.dto.*
import com.clinic.demo.exception.ApiException
import com.clinic.demo.repository.*
import jakarta.transaction.Transactional
import org.mindrot.jbcrypt.BCrypt
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val doctorProfileRepository: DoctorProfileRepository
) {
    // ─── helpers ───
    private fun User.toResponse(): UserResponse = UserResponse(
        id = id!!, mobile = mobile,
        firstName = firstName, lastName = lastName,
        email = email, role = role.name, isActive = isActive,
        createdAt = createdAt.toString()
    )

    private fun findUserOrThrow(id: Long): User =
        userRepository.findById(id).orElseThrow {
            ApiException("کاربر یافت نشد", HttpStatus.NOT_FOUND)
        }

    // ─── me ───
    fun me(userId: Long): UserResponse = findUserOrThrow(userId).toResponse()

    // ─── update profile ───
    @Transactional
    fun updateProfile(userId: Long, req: UpdateUserRequest): UserResponse {
        val u = findUserOrThrow(userId)
        u.firstName = req.firstName
        u.lastName = req.lastName
        u.email = req.email
        u.nationalCode = req.nationalCode
        u.updatedAt = LocalDateTime.now()
        userRepository.save(u)
        return u.toResponse()
    }

    // ─── change password ───
    @Transactional
    fun changePassword(userId: Long, req: ChangePasswordRequest) {
        val u = findUserOrThrow(userId)
        if (!BCrypt.checkpw(req.currentPassword, u.passwordHash))
            throw ApiException("رمز فعلی اشتباه است", HttpStatus.UNAUTHORIZED)
        u.passwordHash = BCrypt.hashpw(req.newPassword, BCrypt.gensalt(12))
        u.updatedAt = LocalDateTime.now()
        userRepository.save(u)
    }

    // ─── list doctors ───
    fun listDoctors(): List<DoctorSummary> =
        userRepository.findAllByRole(UserRole.DOCTOR).map { u ->
            val p = doctorProfileRepository.findByUserId(u.id!!)
            DoctorSummary(
                id = u.id!!, name = u.fullName,
                specialty = p?.specialty,
                consultationFee = p?.consultationFee?.toPlainString()
            )
        }

    // ─── list patients (paginated) ───
    fun listPatients(page: Int, size: Int): List<UserResponse> {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        return userRepository.findAllByRole(UserRole.PATIENT, pageable)
            .content.map { it.toResponse() }
    }

    // ─── list all users (ADMIN, optionally filtered by role) ───
    fun listAll(role: UserRole?, page: Int, size: Int): List<UserResponse> {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        val users = if (role != null)
            userRepository.findAllByRole(role, pageable).content
        else
            userRepository.findAll(pageable).content
        return users.map { it.toResponse() }
    }

    // ─── activate/deactivate ───
    @Transactional
    fun setActive(userId: Long, active: Boolean) {
        val u = findUserOrThrow(userId)
        u.isActive = active
        u.updatedAt = LocalDateTime.now()
        userRepository.save(u)
    }

    // ─── delete user ───
    @Transactional
    fun delete(userId: Long) {
        if (!userRepository.existsById(userId))
            throw ApiException("کاربر یافت نشد", HttpStatus.NOT_FOUND)
        userRepository.deleteById(userId)
    }
    }
