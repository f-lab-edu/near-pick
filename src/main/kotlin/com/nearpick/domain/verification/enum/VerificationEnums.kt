package com.nearpick.domain.verification.enum

enum class VerificationType(val code: String) {
    SIGNUP_EMAIL("signup_email"),
    UPDATE_USER_EMAIL("update_user_email")
}

enum class VerificationStatus {
    PENDING,
    VERIFIED,
    EXPIRED,
    FAILED
}
