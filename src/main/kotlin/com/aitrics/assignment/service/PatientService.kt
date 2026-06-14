package com.aitrics.assignment.service

import com.aitrics.assignment.common.BusinessException
import com.aitrics.assignment.common.ErrorCode
import com.aitrics.assignment.domain.Patient
import com.aitrics.assignment.repository.PatientRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class PatientService(
    private val patientRepository: PatientRepository
) {
    @Transactional
    fun registerPatient(patientId: String, name: String, gender: String, birthDate: LocalDate): Patient {
        if (patientRepository.existsById(patientId)) {
            throw BusinessException(ErrorCode.DUPLICATE_PATIENT)
        }
        val patient = Patient(
            patientId = patientId,
            name = name,
            gender = gender,
            birthDate = birthDate
        )
        return patientRepository.save(patient)
    }

    @Transactional
    fun updatePatient(patientId: String, name: String, gender: String, birthDate: LocalDate, version: Long): Patient {
        val patient = patientRepository.findById(patientId)
            .orElseThrow { BusinessException(ErrorCode.PATIENT_NOT_FOUND) }

        if (patient.version != version) {
            throw BusinessException(ErrorCode.VERSION_CONFLICT)
        }

        patient.name = name
        patient.gender = gender
        patient.birthDate = birthDate
        
        return patientRepository.save(patient)
    }

    fun getPatient(patientId: String): Patient {
        return patientRepository.findById(patientId)
            .orElseThrow { BusinessException(ErrorCode.PATIENT_NOT_FOUND) }
    }
}
