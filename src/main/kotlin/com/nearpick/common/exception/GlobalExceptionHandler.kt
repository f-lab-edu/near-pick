package com.nearpick.common.exception

import com.nearpick.common.response.Response
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<Response<Nothing>> {
        return ResponseEntity.status(ex.status).body(Response.error(ex.code, ex.message))
    }
}
