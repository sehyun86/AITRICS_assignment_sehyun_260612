package com.aitrics.assignment.service

import com.aitrics.assignment.common.BusinessException
import com.aitrics.assignment.common.ErrorCode
import com.aitrics.assignment.domain.Vital
import com.aitrics.assignment.domain.VitalType
import com.aitrics.assignment.repository.PatientRepository
import com.aitrics.assignment.repository.VitalRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class VitalService(
    private val vitalRepository: VitalRepository,
    private val patientRepository: PatientRepository
) {
    @Transactional
    fun upsertVital(
        patientId: String,
        recordedAt: OffsetDateTime,
        vitalType: VitalType,
        value: Double,
        version: Long
    ): Vital {
        // 1. 등록된 환자인지 확인
        if (!patientRepository.existsById(patientId)) {
            throw BusinessException(ErrorCode.PATIENT_NOT_FOUND)
        }

        // 2. 기존 데이터 조회 (UPSERT)
        val existingVital = vitalRepository.findByPatientIdAndRecordedAtAndVitalType(
            patientId, recordedAt, vitalType
        )

        return if (existingVital.isPresent) {
            val vital = existingVital.get()
            // 3. UPDATE 시 낙관적 락 검증
            if (vital.version != version) {
                throw BusinessException(ErrorCode.VERSION_CONFLICT)
            }
            vital.value = value
            vitalRepository.save(vital)
        } else {
            // 4. INSERT 시 version 1로 시작
            val vital = Vital(
                patientId = patientId,
                recordedAt = recordedAt,
                vitalType = vitalType,
                value = value,
                version = 1 // JPA @Version이 알아서 할 수 있지만, 명시적으로 제어 가능
            )
            vitalRepository.save(vital)
        }
    }

    fun getVitals(
        patientId: String,
        from: OffsetDateTime,
        to: OffsetDateTime,
        vitalType: VitalType?
    ): List<Vital> {
        if (!patientRepository.existsById(patientId)) {
            throw BusinessException(ErrorCode.PATIENT_NOT_FOUND)
        }

        return if (vitalType != null) {
            vitalRepository.findByPatientIdAndVitalTypeAndRecordedAtBetween(
                patientId, vitalType, from, to
            )
        } else {
            vitalRepository.findByPatientIdAndRecordedAtBetween(
                patientId, from, to
            )
        }
    }
}
