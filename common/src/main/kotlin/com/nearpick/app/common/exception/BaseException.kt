package com.nearpick.app.common.exception


open class BaseException(
    val code: String,
    override val message: String,
    val category: ErrorCategory
) : RuntimeException(message)


enum class ErrorCategory {
    VALIDATION,
    NOT_FOUND,
    AUTH,
    INTERNAL
}
