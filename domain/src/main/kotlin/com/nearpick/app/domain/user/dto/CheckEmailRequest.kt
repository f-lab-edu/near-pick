package com.nearpick.app.domain.user.dto

import com.nearpick.app.domain.verification.enum.VerificationType

data class CheckEmailRequest(
    val email: String,
    val type: VerificationType
)
