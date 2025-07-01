package com.nearpick.domain.auth.controller

import com.nearpick.common.response.Response
import com.nearpick.domain.auth.dto.LoginRequest
import com.nearpick.domain.auth.dto.LoginResponse
import com.nearpick.domain.auth.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class LoginController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인 후 JWT 토큰을 반환합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "로그인 성공"),
            ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 오류")
        ]
    )
    fun login(@RequestBody request: LoginRequest): Response<LoginResponse> {
        val response = authService.login(request)
        return Response.success(response)
    }
}
