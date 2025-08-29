package com.nearpick.app.domain.verification.service

import com.nearpick.app.domain.verification.enum.VerificationType

interface VerificationService {
    fun sendVerificationCode(type: VerificationType, email: String, subject: String)
    fun verifyCode(type: VerificationType, email: String, token: String): Boolean
    fun isVerifyEmail(type: VerificationType, email: String): Boolean
}
