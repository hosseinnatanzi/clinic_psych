package com.clinic.demo.repository

import com.clinic.demo.domain.Entities.ActivityLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ActivityLogRepository : JpaRepository<ActivityLog, Long>