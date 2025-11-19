package com.nearpick.app.domain.user.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.InvalidPhoneNumberException
import com.nearpick.app.common.validator.Validator

class User(
    val id: String? = null,
    var email: String,
    var nickname: String,
    var password: String,
    var profileImageUrl: String? = null,
    var phoneNumber: String? = null,
    var accountHolderName: String? = null,
    var bankName: String? = null,
    var accountNumber: String? = null,
    var role: Role = Role.USER,
    var isActive: Boolean = true
) {
    init {
        val num: String? = phoneNumber
        if (!num.isNullOrBlank() && !Validator.isValidPhoneNumber(num)) {
            throw InvalidPhoneNumberException(num)
        }
    }

    fun update(
        email: String?,
        nickname: String?,
        profileImageUrl: String?,
        phoneNumber: String?,
        accountHolderName: String?,
        bankName: String?,
        accountNumber: String?
    ) {
        val num: String? = phoneNumber
        if (!num.isNullOrBlank() && !Validator.isValidPhoneNumber(num)) {
            throw InvalidPhoneNumberException(num)
        }
        this.email = email ?: this.email
        this.nickname = nickname ?: this.nickname
        this.profileImageUrl = profileImageUrl ?: this.profileImageUrl
        this.phoneNumber = phoneNumber ?: this.phoneNumber
        this.accountHolderName = accountHolderName ?: this.accountHolderName
        this.bankName = bankName ?: this.bankName
        this.accountNumber = accountNumber ?: this.accountNumber
    }
}

