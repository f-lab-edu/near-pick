package com.nearpick.app.domain.address.dto

import com.nearpick.app.common.exception.InvalidPhoneNumberException
import com.nearpick.app.common.validator.Validator

data class CreateUserAddressRequest(
    val name: String,
    val receiverName: String,
    val phoneNumber: String,
    val fullAddress: String,
    val addressDetail: String?,
    val province: String?,
    val district: String?,
    val neighborhood: String?,
    val street: String?,
    val buildingNumber: String?,
    val isDefault: Boolean?
) {
    init {
        if (!Validator.isValidPhoneNumber(phoneNumber)) {
            throw InvalidPhoneNumberException(phoneNumber)
        }
    }
}
