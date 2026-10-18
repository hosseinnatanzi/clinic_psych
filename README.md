# سیستم مدیریت کلینیک روانشناسی (Psych Clinic)

پروژه Spring Boot (Kotlin) برای مدیریت کلینیک روانشناسی شامل:

- احراز هویت JWT
- مدیریت کاربران (بیمار، منشی، دکتر، ادمین)
- نوبت‌دهی (Appointment)
- پروفایل و برنامه زمانی دکترها
- کلاس‌های گروهی
- تست‌های روانشناسی
- پرداخت آنلاین (زرین‌پال)
- گزارش‌های مالی و عملکردی
- لاگ فعالیت‌ها

## پیش‌نیازها

- JDK 17+
- Maven 3.9+
- PostgreSQL 14+

## راه‌اندازی دیتابیس

```sql
CREATE DATABASE clinic_psych;
```

یا از فایل `src/main/resources/db.migration/V1__init_schema.sql` استفاده کنید.

## اجرا

```bash
# تنظیمات در application-dev.yml
# پیش‌فرض: localhost:5432 / postgres / 25392552

./mvnw spring-boot:run
# یا
mvn spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui.html

کاربر ادمین پیش‌فرض (فقط در profile=dev):
- موبایل: `09120000000`
- رمز: `admin123`

## ساختار API

- `/api/v1/auth/**` — ثبت‌نام و ورود
- `/api/v1/users/**` — مدیریت کاربران
- `/api/v1/doctors/**` — دکترها و برنامه زمانی
- `/api/v1/appointments/**` — نوبت‌ها
- `/api/v1/classes/**` — کلاس‌ها
- `/api/v1/tests/**` — تست‌های روانشناسی
- `/api/v1/payments/**` — پرداخت‌ها
- `/api/v1/reports/**` — گزارش‌ها
- `/health` — سلامت سرویس

## نکات امنیتی

- در production مقدار `JWT_SECRET` را تغییر دهید (حداقل ۳۲ کاراکتر).
- `ZARINPAL_MERCHANT_ID` و `ZARINPAL_SANDBOX=false` را تنظیم کنید.
- `ddl-auto` در prod روی `validate` است.

