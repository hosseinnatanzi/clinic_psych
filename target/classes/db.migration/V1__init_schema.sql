-- ========== USERS ==========
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     mobile VARCHAR(15) NOT NULL UNIQUE,
                                     email VARCHAR(120) UNIQUE,
                                     password_hash VARCHAR(255) NOT NULL,
                                     first_name VARCHAR(60) NOT NULL,
                                     last_name VARCHAR(60) NOT NULL,
                                     national_code VARCHAR(10) UNIQUE,
                                     role VARCHAR(20) NOT NULL,
                                     is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                     updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS doctor_profiles (
                                               id BIGSERIAL PRIMARY KEY,
                                               user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                                               specialty VARCHAR(120),
                                               bio TEXT,
                                               license_number VARCHAR(60),
                                               consultation_fee NUMERIC(12,0) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS doctor_availability (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   doctor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                                   day_of_week INT NOT NULL,
                                                   start_time TIME NOT NULL,
                                                   end_time TIME NOT NULL,
                                                   is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                                   UNIQUE (doctor_id, day_of_week, start_time)
);

CREATE TABLE IF NOT EXISTS doctor_special_days (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   doctor_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                                   date DATE NOT NULL,
                                                   is_available BOOLEAN NOT NULL DEFAULT FALSE,
                                                   start_time TIME,
                                                   end_time TIME,
                                                   note VARCHAR(255),
                                                   UNIQUE (doctor_id, date)
);

CREATE TABLE IF NOT EXISTS appointments (
                                            id BIGSERIAL PRIMARY KEY,
                                            patient_id BIGINT NOT NULL REFERENCES users(id),
                                            doctor_id BIGINT NOT NULL REFERENCES users(id),
                                            start_at TIMESTAMP NOT NULL,
                                            end_at TIMESTAMP NOT NULL,
                                            status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                            notes TEXT,
                                            created_by BIGINT REFERENCES users(id),
                                            created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                            updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_appt_doctor_date ON appointments (doctor_id, start_at);
CREATE INDEX IF NOT EXISTS idx_appt_patient ON appointments (patient_id);

CREATE TABLE IF NOT EXISTS classes (
                                       id BIGSERIAL PRIMARY KEY,
                                       title VARCHAR(200) NOT NULL,
                                       description TEXT,
                                       instructor_id BIGINT REFERENCES users(id),
                                       start_at TIMESTAMP NOT NULL,
                                       end_at TIMESTAMP NOT NULL,
                                       capacity INT NOT NULL DEFAULT 10,
                                       price NUMERIC(12,0) NOT NULL DEFAULT 0,
                                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                       created_by BIGINT REFERENCES users(id),
                                       created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS class_registrations (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   class_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
                                                   patient_id BIGINT NOT NULL REFERENCES users(id),
                                                   registered_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                                   status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED',
                                                   payment_id BIGINT,
                                                   UNIQUE (class_id, patient_id)
);

CREATE TABLE IF NOT EXISTS psychological_tests (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   title VARCHAR(200) NOT NULL,
                                                   description TEXT,
                                                   is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                                   created_by BIGINT REFERENCES users(id),
                                                   created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS test_questions (
                                              id BIGSERIAL PRIMARY KEY,
                                              test_id BIGINT NOT NULL REFERENCES psychological_tests(id) ON DELETE CASCADE,
                                              question_text TEXT NOT NULL,
                                              question_order INT NOT NULL,
                                              question_type VARCHAR(30) NOT NULL DEFAULT 'MULTIPLE_CHOICE'
);

CREATE TABLE IF NOT EXISTS test_options (
                                            id BIGSERIAL PRIMARY KEY,
                                            question_id BIGINT NOT NULL REFERENCES test_questions(id) ON DELETE CASCADE,
                                            option_text TEXT NOT NULL,
                                            score INT NOT NULL DEFAULT 0,
                                            option_order INT NOT NULL
);

CREATE TABLE IF NOT EXISTS test_results (
                                            id BIGSERIAL PRIMARY KEY,
                                            test_id BIGINT NOT NULL REFERENCES psychological_tests(id),
                                            patient_id BIGINT NOT NULL REFERENCES users(id),
                                            doctor_id BIGINT REFERENCES users(id),
                                            total_score INT,
                                            result_summary TEXT,
                                            answers TEXT,
                                            taken_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS payments (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL REFERENCES users(id),
                                        amount NUMERIC(12,0) NOT NULL,
                                        gateway VARCHAR(20) NOT NULL DEFAULT 'ZARINPAL',
                                        authority VARCHAR(120) UNIQUE,
                                        ref_id VARCHAR(120),
                                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                        description VARCHAR(255),
                                        related_type VARCHAR(40),
                                        related_id BIGINT,
                                        paid_at TIMESTAMP,
                                        created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_pay_user ON payments(user_id);
CREATE INDEX IF NOT EXISTS idx_pay_authority ON payments(authority);

CREATE TABLE IF NOT EXISTS activity_logs (
                                             id BIGSERIAL PRIMARY KEY,
                                             user_id BIGINT REFERENCES users(id),
                                             action VARCHAR(100) NOT NULL,
                                             entity_type VARCHAR(50),
                                             entity_id BIGINT,
                                             details TEXT,
                                             ip_address VARCHAR(45),
                                             created_at TIMESTAMP NOT NULL DEFAULT NOW()
);