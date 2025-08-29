package com.nearpick.app.domain.user.dto

import com.nearpick.app.domain.verification.enum.VerificationType

data class CheckEmailVerificationRequest(
    val email: String,
    val token: String,
    val type: VerificationType
)
