package com.clinic.demo.config

import com.clinic.demo.domain.*
import com.clinic.demo.domain.Entities.User
import com.clinic.demo.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
@Component
@Profile("dev")
class DataInitializer(
    private val userRepository: UserRepository
) : CommandLineRunner {

    // ⚠️ نکته‌ی مهم: String (نه String?)
    override fun run(vararg args: String) {
        if (userRepository.findByMobile("09120000000") == null) {
            userRepository.save(
                User(
                    mobile = "09120000000",
                    email = "admin@clinic.local",
                    passwordHash = BCrypt.hashpw("admin123", BCrypt.gensalt(12)),
                    firstName = "Admin",
                    lastName = "Clinic",
                    role = UserRole.ADMIN
                )
            )
            println("✅ Admin user created: mobile=09120000000, password=admin123")
        }
    }
}