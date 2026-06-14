package com.aitrics.assignment.controller

import com.aitrics.assignment.domain.Vital
import com.aitrics.assignment.domain.VitalType
import com.aitrics.assignment.service.VitalService
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime

@Tag(name = "Vital Data Management", description = "생체징후 데이터 관리 API")
@RestController
@RequestMapping("/api/v1")
class VitalController(
    private val vitalService: VitalService
) {
    @Operation(summary = "Vital 데이터 저장/수정 (UPSERT, 낙관적 락 적용)")
    @PostMapping("/vitals")
    @ResponseStatus(HttpStatus.CREATED)
    fun upsert(@Valid @RequestBody request: VitalUpsertRequest): VitalResponse {
        val vital = vitalService.upsertVital(
            request.patientId,
            request.recordedAt,
            request.vitalType,
            request.value,
            request.version
        )
        return VitalResponse.from(vital)
    }

    @Operation(summary = "Vital 데이터 조회")
    @GetMapping("/patients/{patient_id}/vitals")
    fun getVitals(
        @PathVariable("patient_id") patientId: String,
        @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: OffsetDateTime,
        @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: OffsetDateTime,
        @RequestParam("vital_type", required = false) vitalType: VitalType?
    ): VitalListResponse {
        val vitals = vitalService.getVitals(patientId, from, to, vitalType)
        
        // 요구사항에 따른 응답 구조: patient_id, vital_type(요청된 경우), items
        return VitalListResponse(
            patientId = patientId,
            vitalType = vitalType,
            items = vitals.map { VitalItem.from(it) }
        )
    }
}

data class VitalUpsertRequest(
    @get:JsonProperty("patient_id")
    @field:Schema(name = "patient_id", description = "환자 고유 식별자")
    @field:NotBlank(message = "patient_id는 필수입니다.")
    val patientId: String,

    @get:JsonProperty("recorded_at")
    @field:Schema(name = "recorded_at", description = "측정 시점 (ISO 8601)")
    @field:NotNull(message = "recorded_at은 필수입니다.")
    val recordedAt: OffsetDateTime,

    @get:JsonProperty("vital_type")
    @field:Schema(name = "vital_type", description = "Vital 유형 (HR, RR, SBP, DBP, SpO2, BT)")
    @field:NotNull(message = "vital_type은 필수입니다.")
    val vitalType: VitalType,

    @get:JsonProperty("value")
    @field:Schema(name = "value", description = "측정값")
    @field:NotNull(message = "value는 필수입니다.")
    val value: Double,

    @get:JsonProperty("version")
    @field:Schema(name = "version", description = "데이터 버전 (INSERT 시 1, UPDATE 시 DB 버전)")
    @field:NotNull(message = "version은 필수입니다.")
    val version: Long
)

data class VitalResponse(
    @get:JsonProperty("patient_id")
    @field:Schema(name = "patient_id")
    val patientId: String,

    @get:JsonProperty("recorded_at")
    @field:Schema(name = "recorded_at")
    val recordedAt: OffsetDateTime,

    @get:JsonProperty("vital_type")
    @field:Schema(name = "vital_type")
    val vitalType: VitalType,

    @get:JsonProperty("value")
    @field:Schema(name = "value")
    val value: Double,

    @get:JsonProperty("version")
    @field:Schema(name = "version")
    val version: Long
) {
    companion object {
        fun from(vital: Vital) = VitalResponse(
            patientId = vital.patientId,
            recordedAt = vital.recordedAt,
            vitalType = vital.vitalType,
            value = vital.value,
            version = vital.version
        )
    }
}

data class VitalListResponse(
    @get:JsonProperty("patient_id")
    @field:Schema(name = "patient_id")
    val patientId: String,

    @get:JsonProperty("vital_type")
    @field:Schema(name = "vital_type")
    val vitalType: VitalType?,

    @get:JsonProperty("items")
    @field:Schema(name = "items")
    val items: List<VitalItem>
)

data class VitalItem(
    @get:JsonProperty("recorded_at")
    @field:Schema(name = "recorded_at")
    val recordedAt: OffsetDateTime,

    @get:JsonProperty("value")
    @field:Schema(name = "value")
    val value: Double
) {
    companion object {
        fun from(vital: Vital) = VitalItem(
            recordedAt = vital.recordedAt,
            value = vital.value
        )
    }
}
