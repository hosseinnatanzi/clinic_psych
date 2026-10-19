package com.clinic.demo.service

import com.clinic.demo.domain.*
import com.clinic.demo.domain.Entities.*
import com.clinic.demo.dto.*
import com.clinic.demo.exception.ApiException
import com.clinic.demo.repository.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort


@Service
class TestService(
    private val testRepository: PsychologicalTestRepository,
    private val questionRepository: TestQuestionRepository,
    private val optionRepository: TestOptionRepository,
    private val resultRepository: TestResultRepository,
    private val userRepository: UserRepository
) {
    private fun PsychologicalTest.toResponse() = TestResponse(
        id = id!!, title = title, description = description,
        isActive = isActive, createdAt = createdAt.toString()
    )

    private fun TestResult.toResponse() = TestResultResponse(
        id = id!!,
        testId = test.id!!,
        testTitle = test.title,
        patientId = patient.id!!,
        patientName = patient.fullName,
        doctorId = doctor?.id,
        totalScore = totalScore,
        resultSummary = resultSummary,
        takenAt = takenAt.toString()
    )

    private fun findTestOrThrow(id: Long): PsychologicalTest =
        testRepository.findById(id).orElseThrow {
            ApiException("تست یافت نشد", HttpStatus.NOT_FOUND)
        }

    // ─── list ───
    fun listTests(): List<TestResponse> =
        testRepository.findAll().filter { it.isActive }.map { it.toResponse() }

    // ─── create / update / delete ───
    @Transactional
    fun createTest(req: CreateTestRequest, createdBy: Long): Long {
        val creator = userRepository.findById(createdBy).orElseThrow {
            ApiException("کاربر یافت نشد", HttpStatus.NOT_FOUND)
        }
        return testRepository.save(
            PsychologicalTest(title = req.title, description = req.description, createdBy = creator)
        ).id!!
    }

    @Transactional
    fun updateTest(id: Long, req: UpdateTestRequest) {
        val t = findTestOrThrow(id)
        req.title?.let { t.title = it }
        req.description?.let { t.description = it }
        req.isActive?.let { t.isActive = it }
        testRepository.save(t)
    }

    @Transactional
    fun deactivateTest(id: Long) {
        val t = findTestOrThrow(id)
        t.isActive = false
        testRepository.save(t)
    }

    // ─── questions & options ───
    @Transactional
    fun addQuestion(testId: Long, req: AddQuestionRequest): Long {
        val test = findTestOrThrow(testId)
        val type = try { QuestionType.valueOf(req.type) }
        catch (e: Exception) { QuestionType.MULTIPLE_CHOICE }
        return questionRepository.save(
            TestQuestion(
                test = test, questionText = req.text,
                questionOrder = req.order, questionType = type
            )
        ).id!!
    }

    @Transactional
    fun addOption(questionId: Long, req: AddOptionRequest): Long {
        val q = questionRepository.findById(questionId).orElseThrow {
            ApiException("سوال یافت نشد", HttpStatus.NOT_FOUND)
        }
        return optionRepository.save(
            TestOption(
                question = q, optionText = req.text,
                score = req.score, optionOrder = req.order
            )
        ).id!!
    }

    // ─── get full test ───
    fun getTestWithQuestions(testId: Long): TestWithQuestionsResponse {
        val test = findTestOrThrow(testId)
        val questions = questionRepository.findAllByTestIdOrderByQuestionOrder(testId).map { q ->
            QuestionResponse(
                id = q.id!!,
                text = q.questionText,
                type = q.questionType.name,
                options = optionRepository.findAllByQuestionIdOrderByOptionOrder(q.id!!).map { o ->
                    OptionResponse(o.id!!, o.optionText, o.score)
                }
            )
        }
        return TestWithQuestionsResponse(test.id!!, test.title, questions)
    }

    // ─── submit result ───
    @Transactional
    fun submitResult(testId: Long, patientId: Long, req: SubmitResultRequest): Long {
        val test = findTestOrThrow(testId)
        val patient = userRepository.findById(patientId).orElseThrow {
            ApiException("بیمار یافت نشد", HttpStatus.NOT_FOUND)
        }
        return resultRepository.save(
            TestResult(
                test = test, patient = patient,
                totalScore = req.totalScore,
                resultSummary = req.summary,
                answers = req.answersJson
            )
        ).id!!
    }

    // ─── results ───
    fun myResults(patientId: Long): List<TestResultResponse> =
        resultRepository.findAllByPatientId(patientId).map { it.toResponse() }

    fun resultsForPatient(patientId: Long): List<TestResultResponse> =
        resultRepository.findAllByPatientId(patientId).map { it.toResponse() }

    fun allResults(page: Int, size: Int): List<TestResultResponse> {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        return resultRepository.findAll(pageable).content.map { it.toResponse() }
    }
}