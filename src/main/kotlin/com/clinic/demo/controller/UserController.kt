package com.clinic.demo.controller


import com.clinic.demo.domain.UserRole
import com.clinic.demo.dto.*
import com.clinic.demo.security.currentUserId
import com.clinic.demo.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "مدیریت کاربران و پروفایل")
class UserController(private val userService: UserService) {

    @GetMapping("/me")
    @Operation(summary = "پروفایل کاربر جاری")
    fun me(): UserResponse = userService.me(currentUserId())

    @PutMapping("/me")
    @Operation(summary = "ویرایش پروفایل خودم")
    fun updateMe(@Valid @RequestBody req: UpdateUserRequest): UserResponse =
        userService.updateProfile(currentUserId(), req)

    @PutMapping("/me/password")
    @Operation(summary = "تغییر رمز عبور خودم")
    fun changePassword(@Valid @RequestBody req: ChangePasswordRequest): MessageResponse {
        userService.changePassword(currentUserId(), req)
        return MessageResponse("رمز عبور با موفقیت تغییر یافت")
    }

    @GetMapping("/doctors")
    @Operation(summary = "لیست همه‌ی دکترها (برای بیمار)")
    fun listDoctors(): List<DoctorSummary> = userService.listDoctors()

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SECRETARY')")
    @Operation(summary = "لیست بیماران (منشی/مدیر)")
    fun listPatients(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): List<UserResponse> = userService.listPatients(page, size)

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "لیست همه‌ی کاربران (فقط مدیر)")
    fun listAll(
        @RequestParam(required = false) role: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): List<UserResponse> = userService.listAll(role?.let { UserRole.valueOf(it) }, page, size)

    @PutMapping("/{id}/active")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "فعال/غیرفعال کردن کاربر (فقط مدیر)")
    fun setActive(@PathVariable id: Long, @RequestParam active: Boolean): MessageResponse {
        userService.setActive(id, active)
        return MessageResponse(if (active) "کاربر فعال شد" else "کاربر غیرفعال شد")
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "حذف کاربر (فقط مدیر)")
    fun delete(@PathVariable id: Long): MessageResponse {
        userService.delete(id)
        return MessageResponse("کاربر حذف شد")
    }
}