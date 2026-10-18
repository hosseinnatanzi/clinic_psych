package com.clinic.demo.service

import com.clinic.demo.domain.Entities.ClassEntity
import com.clinic.demo.domain.Entities.ClassRegistration


 import com.clinic.demo.domain.*
 import com.clinic.demo.dto.*
import com.clinic.demo.exception.ApiException

import com.clinic.demo.repository.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime



@Service
class ClassService(
    private val classRepository: ClassRepository,
    private val registrationRepository: ClassRegistrationRepository,
    private val userRepository: UserRepository
) {
    private fun ClassEntity.toResponse() = ClassResponse(
        id = id!!, title = title, description = description,
        capacity = capacity, price = price.toPlainString(),
        startAt = startAt.toString(), endAt = endAt.toString(),
        isActive = isActive,
        registeredCount = registrationRepository.countByTrainingClassId(id!!)
    )

    private fun ClassRegistration.toResponse() = ClassRegistrationResponse(
        id = id!!,
        classId = trainingClass.id!!,
        classTitle = trainingClass.title,
        patientId = patient.id!!,
        patientName = patient.fullName,
        status = status.name,
        registeredAt = registeredAt.toString()
    )

    private fun findClassOrThrow(id: Long): ClassEntity =
        classRepository.findById(id).orElseThrow {
            ApiException("کلاس یافت نشد", HttpStatus.NOT_FOUND)
        }

    // ─── CRUD ───
    @Transactional
    fun create(req: CreateClassRequest, createdBy: Long): ClassResponse {
        val creator = userRepository.findById(createdBy).orElseThrow {
            ApiException("کاربر یافت نشد", HttpStatus.NOT_FOUND)
        }
        val instructor = req.instructorId?.let {
            userRepository.findById(it).orElseThrow {
                ApiException("استاد یافت نشد", HttpStatus.NOT_FOUND)
            }
        }
        val c = classRepository.save(
            ClassEntity(
                title = req.title, description = req.description, instructor = instructor,
                startAt = LocalDateTime.parse(req.startAt),
                endAt = LocalDateTime.parse(req.endAt),
                capacity = req.capacity, price = BigDecimal(req.price),
                createdBy = creator
            )
        )
        return c.toResponse()
    }

    fun listActive(): List<ClassResponse> =
        classRepository.findAllByIsActiveTrue().map { it.toResponse() }

    fun listAll(): List<ClassResponse> =
        classRepository.findAll().map { it.toResponse() }

    fun detail(id: Long): ClassResponse = findClassOrThrow(id).toResponse()

    @Transactional
    fun update(id: Long, req: UpdateClassRequest): ClassResponse {
        val c = findClassOrThrow(id)
        req.title?.let { c.title = it }
        req.description?.let { c.description = it }
        req.capacity?.let { c.capacity = it }
        req.price?.let { c.price = BigDecimal(it) }
        req.isActive?.let { c.isActive = it }
        classRepository.save(c)
        return c.toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        val c = findClassOrThrow(id)
        c.isActive = false
        classRepository.save(c)
    }

    // ─── registration ───
    @Transactional
    fun register(classId: Long, patientId: Long) {
        val klass = findClassOrThrow(classId)
        if (!klass.isActive)
            throw ApiException("کلاس غیرفعال است", HttpStatus.BAD_REQUEST)
        if (registrationRepository.countByTrainingClassId(classId) >= klass.capacity)
            throw ApiException("ظرفیت پر است", HttpStatus.CONFLICT)
        if (registrationRepository.findByTrainingClassIdAndPatientId(classId, patientId) != null)
            throw ApiException("قبلاً در این کلاس ثبت‌نام کرده‌اید", HttpStatus.CONFLICT)
        val patient = userRepository.findById(patientId).orElseThrow {
            ApiException("بیمار یافت نشد", HttpStatus.NOT_FOUND)
        }
        registrationRepository.save(ClassRegistration(trainingClass = klass, patient = patient))
    }

    fun myRegistrations(patientId: Long): List<ClassRegistrationResponse> =
        registrationRepository.findAllByPatientId(patientId).map { it.toResponse() }

    @Transactional
    fun cancel(classId: Long, patientId: Long) {
        val r = registrationRepository.findByTrainingClassIdAndPatientId(classId, patientId)
            ?: throw ApiException("ثبت‌نام یافت نشد", HttpStatus.NOT_FOUND)
        r.status = RegistrationStatus.CANCELLED
        registrationRepository.save(r)
    }

    // ─── registrations for a class (منشی/مدیر/دکتر) ───
    fun registrationsForClass(classId: Long): List<ClassRegistrationResponse> {
        findClassOrThrow(classId)
        return registrationRepository.findAllByTrainingClassId(classId).map { it.toResponse() }
    }
}