package com.nearpick.common.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.nearpick.common.response.Response
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class JwtAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"

        val errorResponse = Response.error(
            code = "FORBIDDEN",
            message = "접근 권한이 없습니다."
        )
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
