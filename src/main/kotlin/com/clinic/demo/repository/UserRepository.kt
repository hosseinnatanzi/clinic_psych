package com.clinic.demo.repository


 import com.clinic.demo.domain.Entities.*
 import com.clinic.demo.domain.UserRole
 import org.springframework.data.domain.*
 import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByMobile(mobile: String): User?
    fun findByEmail(email: String): User?
    fun existsByMobile(mobile: String): Boolean
    fun findAllByRole(role: UserRole): List<User>
    fun findAllByRole(role: UserRole, pageable: Pageable): Page<User>
}