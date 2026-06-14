package com.aitrics.assignment.integration

import com.aitrics.assignment.auth.JwtTokenProvider
import com.aitrics.assignment.domain.Patient
import com.aitrics.assignment.repository.PatientRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PatientOptimisticLockTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var patientRepository: PatientRepository

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var token: String

    @BeforeEach
    fun setUp() {
        token = "Bearer " + jwtTokenProvider.createToken("test-user")
    }

    @Test
    @DisplayName("환자 수정 시 버전이 다르면 409 Conflict 발생")
    fun updatePatientOptimisticLockFailure() {
        // Given
        val patientId = "P12345"
        val patient = Patient(patientId, "홍길동", "M", LocalDate.of(1990, 1, 1), version = 1)
        patientRepository.save(patient)

        // When & Then
        val updateRequest = mapOf(
            "name" to "김철수",
            "gender" to "M",
            "birth_date" to "1990-01-01",
            "version" to 0 // DB 버전은 1(초기 저장 시 0에서 1로 증가될 수 있음)인데 0으로 요청
        )

        mockMvc.put("/api/v1/patients/$patientId") {
            header("Authorization", token)
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateRequest)
        }.andExpect {
            status { isConflict() }
            jsonPath("$.title") { value("VERSION_CONFLICT") }
        }
    }
}
