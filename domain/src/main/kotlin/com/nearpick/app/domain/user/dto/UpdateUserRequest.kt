package com.nearpick.app.domain.user.dto

data class UpdateUserRequest(
    val nickname: String?,
    val profileImageUrl: String?,
    val phoneNumber: String?
)
