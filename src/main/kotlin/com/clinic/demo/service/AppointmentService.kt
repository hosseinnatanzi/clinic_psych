package com.clinic.demo.service

import com.clinic.demo.domain.*
import com.clinic.demo.domain.Entities.*
import com.clinic.demo.dto.*
import com.clinic.demo.exception.ApiException
import com.clinic.demo.repository.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository,
    private val availabilityRepository: DoctorAvailabilityRepository
) {
    private fun iranianDow(d: DayOfWeek): Int = when (d) {
        DayOfWeek.SATURDAY  -> 0; DayOfWeek.SUNDAY  -> 1
        DayOfWeek.MONDAY    -> 2; DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4; DayOfWeek.THURSDAY-> 5
        DayOfWeek.FRIDAY    -> 6
    }

    private fun Appointment.toResponse(): AppointmentResponse = AppointmentResponse(
        id = id!!,
        patientId = patient.id!!,
        doctorId = doctor.id!!,
        patientName = patient.fullName,
        doctorName = doctor.fullName,
        startAt = startAt.toString(),
        endAt = endAt.toString(),
        status = status.name,
        notes = notes
    )

    private fun findApptOrThrow(id: Long): Appointment =
        appointmentRepository.findById(id).orElseThrow {
            ApiException("نوبت یافت نشد", HttpStatus.NOT_FOUND)
        }

    // ─── book ───
    @Transactional
    fun book(patientId: Long, req: BookAppointmentRequest): AppointmentResponse {
        val patient = userRepository.findById(patientId).orElseThrow {
            ApiException("بیمار یافت نشد", HttpStatus.NOT_FOUND)
        }
        val doctor = userRepository.findById(req.doctorId).orElseThrow {
            ApiException("دکتر یافت نشد", HttpStatus.NOT_FOUND)
        }
        val startAt = try { LocalDateTime.parse(req.startAt) }
        catch (e: Exception) { throw ApiException("startAt نامعتبر", HttpStatus.BAD_REQUEST) }
        if (!startAt.isAfter(LocalDateTime.now()))
            throw ApiException("زمان انتخابی باید در آینده باشد", HttpStatus.BAD_REQUEST)
        val endAt = startAt.plusMinutes(req.durationMin)
        if (appointmentRepository.existsOverlap(doctor.id!!, startAt, endAt))
            throw ApiException("این بازه قبلاً رزرو شده", HttpStatus.CONFLICT)

        val appt = appointmentRepository.save(
            Appointment(
                patient = patient, doctor = doctor,
                startAt = startAt, endAt = endAt,
                notes = req.notes, createdBy = patient
            )
        )
        return appt.toResponse()
    }

    // ─── change status (با بررسی مالکیت) ───
    @Transactional
    fun changeStatus(id: Long, newStatus: String, actorRole: String, actorId: Long) {
        val appt = findApptOrThrow(id)
        val status = try { AppointmentStatus.valueOf(newStatus) }
        catch (e: Exception) { throw ApiException("status نامعتبر", HttpStatus.BAD_REQUEST) }

        when (actorRole) {
            "PATIENT" -> {
                if (appt.patient.id != actorId)
                    throw ApiException("این نوبت متعلق به شما نیست", HttpStatus.FORBIDDEN)
                if (status != AppointmentStatus.CANCELLED)
                    throw ApiException("بیمار تنها می‌تواند نوبت را لغو کند", HttpStatus.FORBIDDEN)
            }
            "DOCTOR" -> {
                if (appt.doctor.id != actorId)
                    throw ApiException("این نوبت متعلق به شما نیست", HttpStatus.FORBIDDEN)
            }
            // SECRETARY و ADMIN بدون محدودیت
        }

        appt.status = status
        appt.updatedAt = LocalDateTime.now()
        appointmentRepository.save(appt)
    }

    // ─── reschedule (منشی/مدیر) ───
    @Transactional
    fun reschedule(id: Long, req: RescheduleRequest) {
        val appt = findApptOrThrow(id)
        if (appt.status == AppointmentStatus.COMPLETED ||
            appt.status == AppointmentStatus.CANCELLED)
            throw ApiException("این نوبت قابل جابجایی نیست", HttpStatus.BAD_REQUEST)

        val startAt = try { LocalDateTime.parse(req.newStartAt) }
        catch (e: Exception) { throw ApiException("newStartAt نامعتبر", HttpStatus.BAD_REQUEST) }
        if (!startAt.isAfter(LocalDateTime.now()))
            throw ApiException("زمان جدید باید در آینده باشد", HttpStatus.BAD_REQUEST)
        val endAt = startAt.plusMinutes(req.durationMin)

        if (appointmentRepository.existsOverlapExcludingId(
                appt.doctor.id!!, appt.id!!, startAt, endAt))
            throw ApiException("این بازه قبلاً رزرو شده", HttpStatus.CONFLICT)

        appt.startAt = startAt
        appt.endAt = endAt
        appt.updatedAt = LocalDateTime.now()
        appointmentRepository.save(appt)
    }

    // ─── queries ───
    fun myAppointments(patientId: Long): List<AppointmentResponse> =
        appointmentRepository.findAllByPatientId(patientId).map { it.toResponse() }

    fun detail(id: Long): AppointmentResponse = findApptOrThrow(id).toResponse()

    fun doctorAppointments(doctorId: Long): List<AppointmentResponse> =
        appointmentRepository.findAllByDoctorId(doctorId).map { it.toResponse() }

    fun allAppointments(page: Int, size: Int): List<AppointmentResponse> {
        val pageable = PageRequest.of(page, size, Sort.by("startAt").descending())
        return appointmentRepository.findAll(pageable).content.map { it.toResponse() }
    }

    // ─── doctor slots ───
    fun doctorSlots(doctorId: Long, from: String?, days: Int): List<String> {
        val fromDate = try { from?.let(LocalDate::parse) ?: LocalDate.now() }
        catch (e: Exception) { throw ApiException("from نامعتبر", HttpStatus.BAD_REQUEST) }

        val result = mutableListOf<String>()
        var current = fromDate
        repeat(days) {
            val dow = iranianDow(current.dayOfWeek)
            availabilityRepository.findAllByDoctorIdAndIsActiveTrue(doctorId)
                .filter { it.dayOfWeek == dow }
                .forEach { a ->
                    var t: LocalTime = a.startTime
                    while (t.isBefore(a.endTime)) {
                        result += LocalDateTime.of(current, t).toString()
                        t = t.plusMinutes(30)
                    }
                }
            current = current.plusDays(1)
        }
        return result
    }
}