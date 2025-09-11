package com.nearpick.app.common.exception

import com.nearpick.app.common.response.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<Response<Nothing>> {
        val status = when (ex.category) {
            ErrorCategory.VALIDATION -> HttpStatus.BAD_REQUEST
            ErrorCategory.NOT_FOUND  -> HttpStatus.NOT_FOUND
            ErrorCategory.AUTH       -> HttpStatus.UNAUTHORIZED
            ErrorCategory.INTERNAL   -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        return ResponseEntity.status(status).body(Response.error(ex.code, ex.message))
    }
}
