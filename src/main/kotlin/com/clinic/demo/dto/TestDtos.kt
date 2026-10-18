package com.clinic.demo.dto

data class CreateTestRequest(val title: String, val description: String? = null)
data class UpdateTestRequest(val title: String? = null, val description: String? = null, val isActive: Boolean? = null)
data class AddQuestionRequest(val text: String, val order: Int, val type: String = "MULTIPLE_CHOICE")
data class AddOptionRequest(val text: String, val score: Int, val order: Int)
data class SubmitResultRequest(val totalScore: Int?, val summary: String?, val answersJson: String?)

data class TestResponse(
    val id: Long, val title: String, val description: String?,
    val isActive: Boolean, val createdAt: String
)

data class TestWithQuestionsResponse(
    val testId: Long, val title: String,
    val questions: List<QuestionResponse>
)

data class QuestionResponse(
    val id: Long, val text: String, val type: String,
    val options: List<OptionResponse>
)

data class OptionResponse(val id: Long, val text: String, val score: Int)

data class TestResultResponse(
    val id: Long, val testId: Long, val testTitle: String,
    val patientId: Long, val patientName: String,
    val doctorId: Long?, val totalScore: Int?, val resultSummary: String?,
    val takenAt: String
)