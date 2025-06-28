package com.nearpick.common.response

data class SuccessResponse<T>(
    val success: Boolean = true,
    val data: T? = null
)
