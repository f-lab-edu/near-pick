package com.nearpick.common.response

data class Response<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetail? = null
) {
    companion object {
        fun <T> success(data: T): Response<T> =
            Response(success = true, data = data)

        fun error(code: String, message: String): Response<Nothing> =
            Response(success = false, error = ErrorDetail(code, message))
    }
}

data class ErrorDetail(
    val code: String,
    val message: String
)
