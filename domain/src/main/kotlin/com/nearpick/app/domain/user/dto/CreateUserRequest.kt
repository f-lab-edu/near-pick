package com.nearpick.app.domain.user.dto

import com.nearpick.app.common.exception.InvalidPhoneNumberException
import com.nearpick.app.common.validator.Validator

data class CreateUserRequest(
    val email: String,
    val nickname: String,
    val password: String,
    val role: String? = null,
    val profileImageUrl: String? = null,
    val phoneNumber: String? = null,
    val accountHolderName: String? = null,
    val bankName: String? = null,
    val accountNumber: String? = null
)
