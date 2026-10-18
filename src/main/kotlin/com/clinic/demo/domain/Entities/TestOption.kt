package com.clinic.demo.domain.Entities


import jakarta.persistence.*

@Entity
@Table(name = "test_options")
class TestOption(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "question_id", nullable = false)
    var question: TestQuestion,
    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    var optionText: String,
    @Column(nullable = false) var score: Int = 0,
    @Column(name = "option_order", nullable = false) var optionOrder: Int
)