package com.nearpick.app.domain.verification.enum

enum class VerificationType {
    SIGNUP_EMAIL,
    UPDATE_USER_EMAIL
}

enum class VerificationStatus {
    PENDING,
    VERIFIED,
    EXPIRED,
    FAILED
}
