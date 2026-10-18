package com.clinic.demo.repository
import com.clinic.demo.domain.Entities.TestOption
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestOptionRepository : JpaRepository<TestOption, Long> {
    fun findAllByQuestionIdOrderByOptionOrder(questionId: Long): List<TestOption>
}