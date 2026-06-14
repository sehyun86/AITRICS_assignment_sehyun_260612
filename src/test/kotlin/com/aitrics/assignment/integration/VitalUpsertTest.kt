package com.aitrics.assignment.integration

import com.aitrics.assignment.auth.JwtTokenProvider
import com.aitrics.assignment.domain.Patient
import com.aitrics.assignment.repository.PatientRepository
import com.aitrics.assignment.repository.VitalRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.OffsetDateTime

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VitalUpsertTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var patientRepository: PatientRepository

    @Autowired
    lateinit var vitalRepository: VitalRepository

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var token: String

    @BeforeEach
    fun setUp() {
        token = "Bearer " + jwtTokenProvider.createToken("test-user")
        
        // 환자 선등록
        val patient = Patient("P001", "홍길동", "M", LocalDate.of(1990, 1, 1))
        patientRepository.save(patient)
    }

    @Test
    @DisplayName("Vital 데이터 신규 저장 (INSERT)")
    fun insertVital() {
        val request = mapOf(
            "patient_id" to "P001",
            "recorded_at" to "2025-12-01T10:00:00Z",
            "vital_type" to "HR",
            "value" to 80.0,
            "version" to 1
        )

        mockMvc.post("/api/v1/vitals") {
            header("Authorization", token)
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.value") { value(80.0) }
            jsonPath("$.version") { value(1) }
        }
    }

    @Test
    @DisplayName("Vital 데이터 수정 시 버전 불일치하면 409 Conflict")
    fun updateVitalConflict() {
        // Given: 기존 데이터 존재 (version=1)
        val request1 = mapOf(
            "patient_id" to "P001",
            "recorded_at" to "2025-12-01T10:00:00Z",
            "vital_type" to "HR",
            "value" to 80.0,
            "version" to 1
        )
        mockMvc.post("/api/v1/vitals") {
            header("Authorization", token)
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request1)
        }

        // When: 동일 식별자로 다른 버전(2)으로 수정 시도
        val request2 = request1.toMutableMap()
        request2["value"] = 100.0
        request2["version"] = 2 // 현재 DB 버전은 1인데 2로 요청

        // Then
        mockMvc.post("/api/v1/vitals") {
            header("Authorization", token)
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request2)
        }.andExpect {
            status { isConflict() }
        }
    }
}
