package com.clinic.demo.domain
enum class UserRole { PATIENT, SECRETARY, DOCTOR, ADMIN }
enum class AppointmentStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW }
enum class PaymentStatus { PENDING, PAID, FAILED, REFUNDED, CANCELLED }
enum class RegistrationStatus { REGISTERED, CANCELLED, ATTENDED, ABSENT }
enum class QuestionType { MULTIPLE_CHOICE, TEXT, SCALE }
enum class PaymentGateway { ZARINPAL, ZIBAL, MANUAL }