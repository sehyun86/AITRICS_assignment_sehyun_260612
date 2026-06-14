package com.aitrics.assignment.controller

import com.aitrics.assignment.service.InferenceResponse
import com.aitrics.assignment.service.InferenceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Inference API", description = "위험도 추론 API")
@RestController
@RequestMapping("/api/v1/inference")
class InferenceController(
    private val inferenceService: InferenceService
) {
    @Operation(summary = "환자 Vital 위험도 추론")
    @PostMapping("/vital-risk")
    fun inferRisk(@Valid @RequestBody request: InferenceRequest): InferenceResponse {
        return inferenceService.calculateRisk(request.patientId)
    }
}

data class InferenceRequest(
    @field:Schema(name = "patient_id", description = "환자 고유 식별자")
    @field:NotBlank(message = "patient_id는 필수입니다.")
    val patientId: String
)
