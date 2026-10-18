package com.clinic.demo.repository

import com.clinic.demo.domain.Entities.TestQuestion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestQuestionRepository : JpaRepository<TestQuestion, Long> {
    fun findAllByTestIdOrderByQuestionOrder(testId: Long): List<TestQuestion>
}