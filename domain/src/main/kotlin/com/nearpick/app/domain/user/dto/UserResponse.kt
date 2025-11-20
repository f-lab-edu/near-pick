package com.nearpick.app.domain.user.dto

data class UserResponse(
    val id: String?,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val phoneNumber: String?,
    val role: String,
    val accountHolderName: String?,
    val bankName: String?,
    val accountNumber: String?,
    val isActive: Boolean
)
