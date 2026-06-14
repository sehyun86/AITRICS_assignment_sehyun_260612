package com.aitrics.assignment.repository

import com.aitrics.assignment.domain.Vital
import com.aitrics.assignment.domain.VitalType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
interface VitalRepository : JpaRepository<Vital, Long> {
    fun findByPatientIdAndRecordedAtAndVitalType(
        patientId: String,
        recordedAt: OffsetDateTime,
        vitalType: VitalType
    ): Optional<Vital>

    fun findByPatientIdAndRecordedAtBetween(
        patientId: String,
        from: OffsetDateTime,
        to: OffsetDateTime
    ): List<Vital>

    fun findByPatientIdAndVitalTypeAndRecordedAtBetween(
        patientId: String,
        vitalType: VitalType,
        from: OffsetDateTime,
        to: OffsetDateTime
    ): List<Vital>
}
