package com.aitrics.assignment.repository

import com.aitrics.assignment.domain.Patient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PatientRepository : JpaRepository<Patient, String>
