package com.nearpick.common.exception

import org.springframework.http.HttpStatus

open class BaseException(
    val code: String,
    override val message: String,
    val status: HttpStatus
) : RuntimeException(message)
