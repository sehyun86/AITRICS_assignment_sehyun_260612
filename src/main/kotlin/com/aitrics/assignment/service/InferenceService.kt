package com.aitrics.assignment.service

import com.aitrics.assignment.common.BusinessException
import com.aitrics.assignment.common.ErrorCode
import com.aitrics.assignment.domain.VitalType
import com.aitrics.assignment.repository.PatientRepository
import com.aitrics.assignment.repository.VitalRepository
import com.aitrics.assignment.service.inference.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class InferenceService(
    private val vitalRepository: VitalRepository,
    private val patientRepository: PatientRepository,
    @Value("\${app.vital-risk-time-window-hours:24}")
    private val timeWindowHours: Long
) {
    private val rules: List<InferenceRule> = listOf(
        HeartRateRule(),
        SbpRule(),
        Spo2Rule()
    )

    fun calculateRisk(patientId: String): InferenceResponse {
        if (!patientRepository.existsById(patientId)) {
            throw BusinessException(ErrorCode.PATIENT_NOT_FOUND)
        }

        val now = OffsetDateTime.now()
        val from = now.minusHours(timeWindowHours)
        
        val vitals = vitalRepository.findByPatientIdAndRecordedAtBetween(patientId, from, now)
        
        val vitalAverages = rules.associate { rule ->
            val relevantVitals = vitals.filter { it.vitalType == rule.vitalType }
            val average = if (relevantVitals.isNotEmpty()) {
                relevantVitals.map { it.value }.average()
            } else {
                null
            }
            rule.vitalType.name to average
        }

        val triggeredRules = rules.filter { rule ->
            val average = vitalAverages[rule.vitalType.name]
            average != null && rule.evaluate(average)
        }.map { it.ruleDescription }

        val riskLevel = when (triggeredRules.size) {
            0 -> "LOW"
            1, 2 -> "MEDIUM"
            else -> "HIGH"
        }

        return InferenceResponse(
            patientId = patientId,
            riskLevel = riskLevel,
            triggeredRules = triggeredRules,
            vitalAverages = vitalAverages.filterValues { it != null } as Map<String, Double>,
            dataPointsAnalyzed = vitals.size,
            timeRange = TimeRange(from, now),
            evaluatedAt = now
        )
    }
}

data class InferenceResponse(
    val patientId: String,
    val riskLevel: String,
    val triggeredRules: List<String>,
    val vitalAverages: Map<String, Double>,
    val dataPointsAnalyzed: Int,
    val timeRange: TimeRange,
    val evaluatedAt: OffsetDateTime
)

data class TimeRange(
    val from: OffsetDateTime,
    val to: OffsetDateTime
)
