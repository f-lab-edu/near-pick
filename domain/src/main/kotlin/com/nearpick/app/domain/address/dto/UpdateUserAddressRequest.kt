package com.nearpick.app.domain.address.dto

import com.nearpick.app.common.exception.InvalidPhoneNumberException
import com.nearpick.app.common.validator.Validator

data class UpdateUserAddressRequest(
    val name: String?,
    val receiverName: String?,
    val phoneNumber: String?,
    val addressDetail: String?,
    val isDefault: Boolean = false
) {
    init {
        if (!phoneNumber.isNullOrBlank() && !Validator.isValidPhoneNumber(phoneNumber)) {
            throw InvalidPhoneNumberException(phoneNumber)
        }
    }
}
