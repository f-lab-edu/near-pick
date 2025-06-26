package com.nearpick.common.exception

import com.nearpick.common.response.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            code = ex.code,
            message = ex.message
        )
        return ResponseEntity.status(ex.status).body(error)
    }

    // TODO: 필요 시 IllegalArgumentException 등 추가 핸들링 가능
}
