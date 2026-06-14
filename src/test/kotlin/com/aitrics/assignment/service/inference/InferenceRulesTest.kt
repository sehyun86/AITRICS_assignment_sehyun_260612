package com.aitrics.assignment.service.inference

import com.aitrics.assignment.domain.VitalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class InferenceRulesTest {

    @Test
    @DisplayName("HR > 120 규칙 평가: 120 초과 시 true")
    fun heartRateRuleTest() {
        val rule = HeartRateRule()
        assertThat(rule.evaluate(120.1)).isTrue()
        assertThat(rule.evaluate(120.0)).isFalse()
        assertThat(rule.evaluate(119.9)).isFalse()
    }

    @Test
    @DisplayName("SBP < 90 규칙 평가: 90 미만 시 true")
    fun sbpRuleTest() {
        val rule = SbpRule()
        assertThat(rule.evaluate(89.9)).isTrue()
        assertThat(rule.evaluate(90.0)).isFalse()
        assertThat(rule.evaluate(90.1)).isFalse()
    }

    @Test
    @DisplayName("SpO2 < 90 규칙 평가: 90 미만 시 true")
    fun spo2RuleTest() {
        val rule = Spo2Rule()
        assertThat(rule.evaluate(89.9)).isTrue()
        assertThat(rule.evaluate(90.0)).isFalse()
        assertThat(rule.evaluate(90.1)).isFalse()
    }
}
