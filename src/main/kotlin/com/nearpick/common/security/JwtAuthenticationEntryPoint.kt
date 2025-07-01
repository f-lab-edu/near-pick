package com.nearpick.common.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.nearpick.common.response.Response
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"

        val errorResponse = Response.error(
            code = "UNAUTHORIZED",
            message = "인증에 실패했습니다. 다시 로그인해주세요."
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
