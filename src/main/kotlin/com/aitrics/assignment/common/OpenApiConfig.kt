package com.aitrics.assignment.common

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "AITRICS Patient Monitoring API",
        version = "v1",
        description = "환자 생체징후 데이터 관리 및 위험도 추론 시스템 API 명세"
    ),
    security = [SecurityRequirement(name = "Bearer Auth")]
)
@SecurityScheme(
    name = "Bearer Auth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
class OpenApiConfig
