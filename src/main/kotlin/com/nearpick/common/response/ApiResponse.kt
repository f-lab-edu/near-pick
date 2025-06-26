package com.nearpick.common.response

data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null
)
