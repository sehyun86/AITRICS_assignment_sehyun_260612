package com.aitrics.assignment.service.inference

import com.aitrics.assignment.domain.VitalType

data class InferenceResult(
    val ruleName: String,
    val isTriggered: Boolean,
    val averageValue: Double?
)

interface InferenceRule {
    val vitalType: VitalType
    val ruleDescription: String
    fun evaluate(averageValue: Double): Boolean
}

class HeartRateRule : InferenceRule {
    override val vitalType = VitalType.HR
    override val ruleDescription = "HR > 120"
    override fun evaluate(averageValue: Double): Boolean = averageValue > 120
}

class SbpRule : InferenceRule {
    override val vitalType = VitalType.SBP
    override val ruleDescription = "SBP < 90"
    override fun evaluate(averageValue: Double): Boolean = averageValue < 90
}

class Spo2Rule : InferenceRule {
    override val vitalType = VitalType.SpO2
    override val ruleDescription = "SpO2 < 90"
    override fun evaluate(averageValue: Double): Boolean = averageValue < 90
}
