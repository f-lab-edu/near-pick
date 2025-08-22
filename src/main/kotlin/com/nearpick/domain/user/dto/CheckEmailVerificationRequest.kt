package com.nearpick.domain.user.dto

import com.nearpick.domain.verification.enum.VerificationType

data class CheckEmailVerificationRequest(
    val email: String,
    val token: String,
    val type: VerificationType
)
