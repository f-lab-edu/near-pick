package com.nearpick.app.domain.user.dto

data class UpdateUserRequest(
    val email: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val phoneNumber: String?,
    var accountHolderName: String?,
    var bankName: String?,
    var accountNumber: String?
)
