package com.nearpick.domain.user.dto

data class UserUpdateRequest(
    val nickname: String?,
    val profileImageUrl: String?,
    val phoneNumber: String?
)
