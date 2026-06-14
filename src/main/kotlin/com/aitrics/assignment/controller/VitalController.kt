package com.aitrics.assignment.controller

import com.aitrics.assignment.domain.Vital
import com.aitrics.assignment.domain.VitalType
import com.aitrics.assignment.service.VitalService
import io.swagger.v3.oas.annotations.Operation
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
    @field:NotBlank(message = "patient_id는 필수입니다.")
    val patientId: String,
    @field:NotNull(message = "recorded_at은 필수입니다.")
    val recordedAt: OffsetDateTime,
    @field:NotNull(message = "vital_type은 필수입니다.")
    val vitalType: VitalType,
    @field:NotNull(message = "value는 필수입니다.")
    val value: Double,
    @field:NotNull(message = "version은 필수입니다.")
    val version: Long
)

data class VitalResponse(
    val patientId: String,
    val recordedAt: OffsetDateTime,
    val vitalType: VitalType,
    val value: Double,
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
    val patientId: String,
    val vitalType: VitalType?,
    val items: List<VitalItem>
)

data class VitalItem(
    val recordedAt: OffsetDateTime,
    val value: Double
) {
    companion object {
        fun from(vital: Vital) = VitalItem(
            recordedAt = vital.recordedAt,
            value = vital.value
        )
    }
}
