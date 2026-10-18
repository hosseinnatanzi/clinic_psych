package com.clinic.demo.repository
import com.clinic.demo.domain.Entities.ClassEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClassRepository : JpaRepository<ClassEntity, Long> {
    fun findAllByIsActiveTrue(): List<ClassEntity>
}