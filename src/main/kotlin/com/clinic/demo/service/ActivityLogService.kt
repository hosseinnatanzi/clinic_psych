package com.clinic.demo.service



import com.clinic.demo.domain.*
import com.clinic.demo.domain.Entities.ActivityLog
import com.clinic.demo.repository.*
 import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActivityLogService(
    private val logRepository: ActivityLogRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun log(userId: Long?, action: String, entityType: String? = null,
            entityId: Long? = null, details: String? = null, ipAddress: String? = null) {
        logRepository.save(
            ActivityLog(
                user = userId?.let { userRepository.findById(it).orElse(null) },
                action = action, entityType = entityType,
                entityId = entityId, details = details, ipAddress = ipAddress
            )
        )
    }
}