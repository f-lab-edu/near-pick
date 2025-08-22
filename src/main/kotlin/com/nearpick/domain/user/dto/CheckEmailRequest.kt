package com.nearpick.domain.user.dto

import com.nearpick.domain.verification.enum.VerificationType

data class CheckEmailRequest(
    val email: String,
    val type: VerificationType
)
