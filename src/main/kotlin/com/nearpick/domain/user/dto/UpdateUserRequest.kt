package com.nearpick.domain.user.dto

data class UpdateUserRequest(
    val nickname: String?,
    val profileImageUrl: String?,
    val phoneNumber: String?
)
