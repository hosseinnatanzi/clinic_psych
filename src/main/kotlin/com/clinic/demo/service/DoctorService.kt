package com.clinic.demo.service


import com.clinic.demo.domain.*
import com.clinic.demo.domain.Entities.*
import com.clinic.demo.dto.*
import com.clinic.demo.exception.ApiException
import com.clinic.demo.repository.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class DoctorService(
    private val userRepository: UserRepository,
    private val profileRepository: DoctorProfileRepository,
    private val availabilityRepository: DoctorAvailabilityRepository,
    private val specialDayRepository: DoctorSpecialDayRepository
) {
    private fun iranianDow(d: DayOfWeek): Int = when (d) {
        DayOfWeek.SATURDAY  -> 0; DayOfWeek.SUNDAY  -> 1
        DayOfWeek.MONDAY    -> 2; DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4; DayOfWeek.THURSDAY-> 5
        DayOfWeek.FRIDAY    -> 6
    }

    private fun findDoctor(id: Long): User =
        userRepository.findById(id).orElseThrow {
            ApiException("دکتر یافت نشد", HttpStatus.NOT_FOUND)
        }.also {
            if (it.role != UserRole.DOCTOR)
                throw ApiException("این کاربر دکتر نیست", HttpStatus.BAD_REQUEST)
        }

    // ─── list ───
    fun listDoctors(): List<DoctorSummary> =
        userRepository.findAllByRole(UserRole.DOCTOR).map { u ->
            val p = profileRepository.findByUserId(u.id!!)
            DoctorSummary(
                id = u.id!!, name = u.fullName,
                specialty = p?.specialty,
                consultationFee = p?.consultationFee?.toPlainString()
            )
        }

    fun detail(id: Long): Map<String, Any?> {
        val u = findDoctor(id)
        val p = profileRepository.findByUserId(id)
        return mapOf(
            "id" to u.id, "name" to u.fullName, "mobile" to u.mobile,
            "email" to u.email, "specialty" to p?.specialty,
            "bio" to p?.bio, "consultationFee" to p?.consultationFee?.toPlainString()
        )
    }

    // ─── profile ───
    @Transactional
    fun updateProfile(doctorId: Long, req: UpdateDoctorProfileRequest) {
        val u = findDoctor(doctorId)
        var p = profileRepository.findByUserId(doctorId) ?: DoctorProfile(user = u)
        req.specialty?.let { p.specialty = it }
        req.bio?.let { p.bio = it }
        req.licenseNumber?.let { p.licenseNumber = it }
        req.consultationFee?.let { p.consultationFee = BigDecimal(it) }
        profileRepository.save(p)
    }

    // ─── weekly availability ───
    fun weekly(doctorId: Long): List<Map<String, Any?>> =
        availabilityRepository.findAllByDoctorId(doctorId).map {
            mapOf(
                "id" to it.id, "dayOfWeek" to it.dayOfWeek,
                "start" to it.startTime.toString(),
                "end" to it.endTime.toString(),
                "active" to it.isActive
            )
        }

    @Transactional
    fun addWeekly(doctorId: Long, req: WeeklyAvailabilityRequest) {
        val u = findDoctor(doctorId)
        if (req.dayOfWeek !in 0..6)
            throw ApiException("dayOfWeek باید بین 0 تا 6 باشد", HttpStatus.BAD_REQUEST)
        availabilityRepository.save(
            DoctorAvailability(
                doctor = u, dayOfWeek = req.dayOfWeek,
                startTime = LocalTime.parse(req.startTime),
                endTime = LocalTime.parse(req.endTime)
            )
        )
    }

    @Transactional
    fun deleteWeekly(id: Long, doctorId: Long) {
        val a = availabilityRepository.findById(id).orElseThrow {
            ApiException("بازه یافت نشد", HttpStatus.NOT_FOUND)
        }
        if (a.doctor.id != doctorId) throw ApiException("دسترسی ندارید", HttpStatus.FORBIDDEN)
        availabilityRepository.delete(a)
    }

    // ─── special days ───
    fun specialDays(doctorId: Long): List<Map<String, Any?>> =
        specialDayRepository.findAllByDoctorId(doctorId).map {
            mapOf(
                "id" to it.id, "date" to it.date.toString(),
                "available" to it.isAvailable,
                "start" to it.startTime?.toString(),
                "end" to it.endTime?.toString(),
                "note" to it.note
            )
        }

    @Transactional
    fun addSpecialDay(doctorId: Long, req: SpecialDayRequest) {
        val u = findDoctor(doctorId)
        specialDayRepository.save(
            DoctorSpecialDay(
                doctor = u, date = LocalDate.parse(req.date),
                isAvailable = req.available,
                startTime = req.startTime?.let(LocalTime::parse),
                endTime = req.endTime?.let(LocalTime::parse),
                note = req.note
            )
        )
    }

    @Transactional
    fun deleteSpecialDay(id: Long, doctorId: Long) {
        val d = specialDayRepository.findById(id).orElseThrow {
            ApiException("روز یافت نشد", HttpStatus.NOT_FOUND)
        }
        if (d.doctor.id != doctorId) throw ApiException("دسترسی ندارید", HttpStatus.FORBIDDEN)
        specialDayRepository.delete(d)
    }

    // ─── slots ───
    fun slots(doctorId: Long, from: LocalDate, days: Int): List<String> {
        val result = mutableListOf<String>()
        var current = from
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