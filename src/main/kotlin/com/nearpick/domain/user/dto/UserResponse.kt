package com.nearpick.domain.user.dto

data class UserResponse(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val phoneNumber: String?,
    val role: String,
    val isActive: Boolean
)
