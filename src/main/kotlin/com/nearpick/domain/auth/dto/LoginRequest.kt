package com.nearpick.domain.auth.dto

data class LoginRequest(
    val email: String,
    val password: String
)
