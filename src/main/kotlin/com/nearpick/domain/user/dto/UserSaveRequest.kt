package com.nearpick.domain.user.dto

data class UserSaveRequest(
    val email: String,
    val nickname: String,
    val password: String,
    val profileImageUrl: String? = null,
    val phoneNumber: String? = null
)
