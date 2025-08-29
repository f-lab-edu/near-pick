package com.nearpick.app.domain.auth.dto

data class LoginRequest(
    val email: String,
    val password: String
)
