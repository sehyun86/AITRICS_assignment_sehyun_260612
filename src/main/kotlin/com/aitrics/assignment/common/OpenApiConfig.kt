package com.aitrics.assignment.common

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "AITRICS Patient Monitoring API",
        version = "v1",
        description = "환자 생체징후 데이터 관리 및 위험도 추론 시스템 API 명세"
    )
)
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val securitySchemeName = "Bearer Auth"
        val securityRequirement = SecurityRequirement().addList(securitySchemeName)
        val components = Components().addSecuritySchemes(
            securitySchemeName,
            SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        )

        return OpenAPI()
            .addSecurityItem(securityRequirement)
            .setComponents(components)
    }
}
