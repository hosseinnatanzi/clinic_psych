package com.clinic.demo.repository

import com.clinic.demo.domain.Entities.PsychologicalTest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PsychologicalTestRepository : JpaRepository<PsychologicalTest, Long>