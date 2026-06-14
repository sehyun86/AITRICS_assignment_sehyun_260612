package com.aitrics.assignment.controller

import com.aitrics.assignment.domain.Patient
import com.aitrics.assignment.service.PatientService
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "Patient Management", description = "환자 관리 API")
@RestController
@RequestMapping("/api/v1/patients")
class PatientController(
    private val patientService: PatientService
) {
    @Operation(summary = "환자 등록")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: PatientRegisterRequest): PatientResponse {
        val patient = patientService.registerPatient(
            request.patientId,
            request.name,
            request.gender,
            request.birthDate
        )
        return PatientResponse.from(patient)
    }

    @Operation(summary = "환자 정보 수정 (낙관적 락 적용)")
    @PutMapping("/{patient_id}")
    fun update(
        @PathVariable("patient_id") patientId: String,
        @Valid @RequestBody request: PatientUpdateRequest
    ): PatientResponse {
        val patient = patientService.updatePatient(
            patientId,
            request.name,
            request.gender,
            request.birthDate,
            request.version
        )
        return PatientResponse.from(patient)
    }

    @Operation(summary = "전체 환자 목록 조회")
    @GetMapping
    fun getAllPatients(): List<PatientResponse> {
        return patientService.getAllPatients().map { PatientResponse.from(it) }
    }

    @Operation(summary = "개별 환자 조회")
    @GetMapping("/{patient_id}")
    fun getPatient(@PathVariable("patient_id") patientId: String): PatientResponse {
        val patient = patientService.getPatient(patientId)
        return PatientResponse.from(patient)
    }
}

data class PatientRegisterRequest(
    @get:JsonProperty("patient_id")
    @field:Schema(name = "patient_id", description = "환자 고유 식별자")
    @field:NotBlank(message = "patient_id는 필수입니다.")
    val patientId: String,

    @get:JsonProperty("name")
    @field:Schema(name = "name", description = "환자 성명")
    @field:NotBlank(message = "name은 필수입니다.")
    val name: String,

    @get:JsonProperty("gender")
    @field:Schema(name = "gender", description = "성별 (M/F)")
    @field:NotBlank(message = "gender는 필수입니다.")
    val gender: String,

    @get:JsonProperty("birth_date")
    @field:Schema(name = "birth_date", description = "생년월일 (YYYY-MM-DD)")
    @field:NotNull(message = "birth_date는 필수입니다.")
    val birthDate: LocalDate
)

data class PatientUpdateRequest(
    @get:JsonProperty("name")
    @field:Schema(name = "name", description = "환자 성명")
    @field:NotBlank(message = "name은 필수입니다.")
    val name: String,

    @get:JsonProperty("gender")
    @field:Schema(name = "gender", description = "성별 (M/F)")
    @field:NotBlank(message = "gender는 필수입니다.")
    val gender: String,

    @get:JsonProperty("birth_date")
    @field:Schema(name = "birth_date", description = "생년월일 (YYYY-MM-DD)")
    @field:NotNull(message = "birth_date는 필수입니다.")
    val birthDate: LocalDate,

    @get:JsonProperty("version")
    @field:Schema(name = "version", description = "데이터 버전 (낙관적 락 용)")
    @field:NotNull(message = "version은 필수입니다.")
    val version: Long
)

data class PatientResponse(
    @get:JsonProperty("patient_id")
    @field:Schema(name = "patient_id")
    val patientId: String,

    @get:JsonProperty("name")
    @field:Schema(name = "name")
    val name: String,

    @get:JsonProperty("gender")
    @field:Schema(name = "gender")
    val gender: String,

    @get:JsonProperty("birth_date")
    @field:Schema(name = "birth_date")
    val birthDate: LocalDate,

    @get:JsonProperty("version")
    @field:Schema(name = "version")
    val version: Long
) {
    companion object {
        fun from(patient: Patient) = PatientResponse(
            patientId = patient.patientId,
            name = patient.name,
            gender = patient.gender,
            birthDate = patient.birthDate,
            version = patient.version
        )
    }
}
