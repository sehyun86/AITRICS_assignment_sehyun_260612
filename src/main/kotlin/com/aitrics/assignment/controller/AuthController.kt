package com.aitrics.assignment.controller

import com.aitrics.assignment.auth.JwtTokenProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth Utility", description = "테스트용 토큰 발급 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenProvider: JwtTokenProvider
) {
    @Operation(summary = "테스트용 JWT 토큰 생성", description = "입력한 username으로 유효한 토큰을 생성합니다.")
    @GetMapping("/token")
    fun getToken(@RequestParam("username") username: String): TokenResponse {
        val token = jwtTokenProvider.createToken(username)
        return TokenResponse(token)
    }
}

data class TokenResponse(val token: String)
