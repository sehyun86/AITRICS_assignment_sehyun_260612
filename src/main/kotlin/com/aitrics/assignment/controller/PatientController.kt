package com.aitrics.assignment.controller

import com.aitrics.assignment.domain.Patient
import com.aitrics.assignment.service.PatientService
import io.swagger.v3.oas.annotations.Operation
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
}

data class PatientRegisterRequest(
    @field:NotBlank(message = "patient_id는 필수입니다.")
    val patientId: String,
    @field:NotBlank(message = "name은 필수입니다.")
    val name: String,
    @field:NotBlank(message = "gender는 필수입니다.")
    val gender: String,
    @field:NotNull(message = "birth_date는 필수입니다.")
    val birthDate: LocalDate
)

data class PatientUpdateRequest(
    @field:NotBlank(message = "name은 필수입니다.")
    val name: String,
    @field:NotBlank(message = "gender는 필수입니다.")
    val gender: String,
    @field:NotNull(message = "birth_date는 필수입니다.")
    val birthDate: LocalDate,
    @field:NotNull(message = "version은 필수입니다.")
    val version: Long
)

data class PatientResponse(
    val patientId: String,
    val name: String,
    val gender: String,
    val birthDate: LocalDate,
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
