package com.nearpick.domain.address.dto

import com.nearpick.common.exception.InvalidPhoneNumberException
import com.nearpick.common.validator.Validator

data class UpdateUserAddressRequest(
    val name: String,
    val receiverName: String,
    val phoneNumber: String,
    val addressDetail: String?,
    val isDefault: Boolean = false
) {
    init {
        if (!Validator.isValidPhoneNumber(phoneNumber)) {
            throw InvalidPhoneNumberException(phoneNumber)
        }
    }
}
