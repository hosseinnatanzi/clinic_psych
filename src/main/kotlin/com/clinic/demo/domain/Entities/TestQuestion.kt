package com.clinic.demo.domain.Entities

import com.clinic.demo.domain.QuestionType
import jakarta.persistence.*

@Entity
@Table(name = "test_questions")
class TestQuestion(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "test_id", nullable = false)
    var test: PsychologicalTest,
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    var questionText: String,
    @Column(name = "question_order", nullable = false) var questionOrder: Int,
    @Enumerated(EnumType.STRING) @Column(name = "question_type", nullable = false, length = 30)
    var questionType: QuestionType = QuestionType.MULTIPLE_CHOICE
)