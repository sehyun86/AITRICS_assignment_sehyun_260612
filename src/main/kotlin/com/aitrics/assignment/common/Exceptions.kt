package com.aitrics.assignment.common

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.Instant

enum class ErrorCode(val status: HttpStatus, val message: String) {
    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "환자를 찾을 수 없습니다."),
    VERSION_CONFLICT(HttpStatus.CONFLICT, "데이터 버전이 일치하지 않습니다. 다른 사용자에 의해 수정되었을 수 있습니다."),
    INVALID_VITAL_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 Vital 타입입니다."),
    DUPLICATE_PATIENT(HttpStatus.BAD_REQUEST, "이미 등록된 환자 ID입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.")
}

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(ex.errorCode.status, ex.errorCode.message)
        problemDetail.title = ex.errorCode.name
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }

    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException::class)
    fun handleOptimisticLockingFailureException(ex: org.springframework.orm.ObjectOptimisticLockingFailureException): ProblemDetail {
        val errorCode = ErrorCode.VERSION_CONFLICT
        val problemDetail = ProblemDetail.forStatusAndDetail(errorCode.status, errorCode.message)
        problemDetail.title = errorCode.name
        problemDetail.setProperty("timestamp", Instant.now())
        return problemDetail
    }
}

class BusinessException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
